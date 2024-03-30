/*
 *  Copyright 2017-2024 Adobe.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cc.iotkit.s3mock.store;

import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_SERVER_SIDE_ENCRYPTION_AWS_KMS_KEY_ID;
import static cc.iotkit.s3mock.util.DigestUtil.hexDigest;
import static cc.iotkit.s3mock.util.XmlUtil.deserializeJaxb;
import static cc.iotkit.s3mock.util.XmlUtil.serializeJaxb;
import static java.nio.file.Files.newOutputStream;

import cc.iotkit.s3mock.util.AwsChecksumInputStream;
import cc.iotkit.s3mock.util.AwsChunkedDecodingChecksumInputStream;
import cc.iotkit.s3mock.util.AwsChunkedDecodingInputStream;
import cc.iotkit.s3mock.dto.AccessControlPolicy;
import cc.iotkit.s3mock.dto.ChecksumAlgorithm;
import cc.iotkit.s3mock.dto.CopyObjectResult;
import cc.iotkit.s3mock.dto.Grant;
import cc.iotkit.s3mock.dto.Grantee;
import cc.iotkit.s3mock.dto.LegalHold;
import cc.iotkit.s3mock.dto.Owner;
import cc.iotkit.s3mock.dto.Retention;
import cc.iotkit.s3mock.dto.StorageClass;
import cc.iotkit.s3mock.dto.Tag;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores objects and their metadata created in S3Mock.
 */
public class ObjectStore {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectStore.class);
    private static final String META_FILE = "objectMetadata.json";
    private static final String ACL_FILE = "objectAcl.xml";
    private static final String DATA_FILE = "binaryData";

    /**
     * This map stores one lock object per S3Object ID.
     * Any method modifying the underlying file must acquire the lock object before the modification.
     */
    private final Map<UUID, Object> lockStore = new ConcurrentHashMap<>();

    private final boolean retainFilesOnExit;
    private final DateTimeFormatter s3ObjectDateFormat;

    private final ObjectMapper objectMapper;

    public ObjectStore(boolean retainFilesOnExit,
                       DateTimeFormatter s3ObjectDateFormat, ObjectMapper objectMapper) {
        this.retainFilesOnExit = retainFilesOnExit;
        this.s3ObjectDateFormat = s3ObjectDateFormat;
        this.objectMapper = objectMapper;
    }

    /**
     * Stores an object inside a Bucket.
     *
     * @param bucket                        Bucket to store the object in.
     * @param id                            object ID
     * @param key                           object key to be stored.
     * @param contentType                   The Content Type.
     * @param storeHeaders                  Various headers to store, like Content Encoding.
     * @param dataStream                    The InputStream to store.
     * @param useV4ChunkedWithSigningFormat If {@code true}, V4-style signing is enabled.
     * @param userMetadata                  User metadata to store for this object, will be available for the
     *                                      object with the key prefixed with "x-amz-meta-".
     * @param etag                          the etag. If null, etag will be computed by this method.
     * @param tags                          The tags to store.
     * @return {@link S3ObjectMetadata}.
     */
    public S3ObjectMetadata storeS3ObjectMetadata(BucketMetadata bucket,
                                                  UUID id,
                                                  String key,
                                                  String contentType,
                                                  Map<String, String> storeHeaders,
                                                  InputStream dataStream,
                                                  boolean useV4ChunkedWithSigningFormat,
                                                  Map<String, String> userMetadata,
                                                  Map<String, String> encryptionHeaders,
                                                  String etag,
                                                  List<Tag> tags,
                                                  ChecksumAlgorithm checksumAlgorithm,
                                                  String checksum,
                                                  Owner owner,
                                                  StorageClass storageClass) {
        lockStore.putIfAbsent(id, new Object());
        synchronized (lockStore.get(id)) {
            createObjectRootFolder(bucket, id);
            var checksumEmbedded = checksumAlgorithm != null && checksum == null;
            var inputStream = wrapStream(dataStream, useV4ChunkedWithSigningFormat, checksumEmbedded);
            var dataFile = inputStreamToFile(inputStream, getDataFilePath(bucket, id));
            if (inputStream instanceof AwsChecksumInputStream) {
                checksum = ((AwsChecksumInputStream) inputStream).getChecksum();
            }
            var now = Instant.now();
            var s3ObjectMetadata = new S3ObjectMetadata(
                    id,
                    key,
                    Long.toString(dataFile.length()),
                    s3ObjectDateFormat.format(now),
                    etag != null
                            ? etag
                            : hexDigest(encryptionHeaders.get(X_AMZ_SERVER_SIDE_ENCRYPTION_AWS_KMS_KEY_ID),
                            dataFile),
                    contentType,
                    now.toEpochMilli(),
                    dataFile.toPath(),
                    userMetadata,
                    tags,
                    null,
                    null,
                    owner,
                    storeHeaders,
                    encryptionHeaders,
                    checksumAlgorithm,
                    checksum,
                    storageClass
            );
            writeMetafile(bucket, s3ObjectMetadata);
            return s3ObjectMetadata;
        }
    }

    private AccessControlPolicy privateCannedAcl(Owner owner) {
        var grant = new Grant(Grantee.from(owner), Grant.Permission.FULL_CONTROL);
        return new AccessControlPolicy(owner, Collections.singletonList(grant));
    }

    /**
     * Store tags for a given object.
     *
     * @param bucket Bucket the object is stored in.
     * @param id     object ID to store tags for.
     * @param tags   List of tagSet objects.
     */
    public void storeObjectTags(BucketMetadata bucket, UUID id, List<Tag> tags) {
        synchronized (lockStore.get(id)) {
            var s3ObjectMetadata = getS3ObjectMetadata(bucket, id);
            writeMetafile(bucket, new S3ObjectMetadata(
                    s3ObjectMetadata.getId(),
                    s3ObjectMetadata.getKey(),
                    s3ObjectMetadata.getSize(),
                    s3ObjectMetadata.getModificationDate(),
                    s3ObjectMetadata.getEtag(),
                    s3ObjectMetadata.getContentType(),
                    s3ObjectMetadata.getLastModified(),
                    s3ObjectMetadata.getDataPath(),
                    s3ObjectMetadata.getUserMetadata(),
                    tags,
                    s3ObjectMetadata.getLegalHold(),
                    s3ObjectMetadata.getRetention(),
                    s3ObjectMetadata.getOwner(),
                    s3ObjectMetadata.getStoreHeaders(),
                    s3ObjectMetadata.getEncryptionHeaders(),
                    s3ObjectMetadata.getChecksumAlgorithm(),
                    s3ObjectMetadata.getChecksum(),
                    s3ObjectMetadata.getStorageClass()
            ));
        }
    }

    /**
     * Store legal hold for a given object.
     *
     * @param bucket    Bucket the object is stored in.
     * @param id        object ID to store tags for.
     * @param legalHold the legal hold.
     */
    public void storeLegalHold(BucketMetadata bucket, UUID id, LegalHold legalHold) {
        synchronized (lockStore.get(id)) {
            var s3ObjectMetadata = getS3ObjectMetadata(bucket, id);
            writeMetafile(bucket, new S3ObjectMetadata(
                    s3ObjectMetadata.getId(),
                    s3ObjectMetadata.getKey(),
                    s3ObjectMetadata.getSize(),
                    s3ObjectMetadata.getModificationDate(),
                    s3ObjectMetadata.getEtag(),
                    s3ObjectMetadata.getContentType(),
                    s3ObjectMetadata.getLastModified(),
                    s3ObjectMetadata.getDataPath(),
                    s3ObjectMetadata.getUserMetadata(),
                    s3ObjectMetadata.getTags(),
                    legalHold,
                    s3ObjectMetadata.getRetention(),
                    s3ObjectMetadata.getOwner(),
                    s3ObjectMetadata.getStoreHeaders(),
                    s3ObjectMetadata.getEncryptionHeaders(),
                    s3ObjectMetadata.getChecksumAlgorithm(),
                    s3ObjectMetadata.getChecksum(),
                    s3ObjectMetadata.getStorageClass()
            ));
        }
    }

    /**
     * Store ACL for a given object.
     *
     * @param bucket Bucket the object is stored in.
     * @param id     object ID to store tags for.
     * @param policy the ACL.
     */
    public void storeAcl(BucketMetadata bucket, UUID id, AccessControlPolicy policy) {
        writeAclFile(bucket, id, policy);
    }

    public AccessControlPolicy readAcl(BucketMetadata bucket, UUID id) {
        var policy = readAclFile(bucket, id);
        if (policy == null) {
            var s3ObjectMetadata = getS3ObjectMetadata(bucket, id);
            return privateCannedAcl(s3ObjectMetadata.getOwner());
        }
        return policy;
    }

    /**
     * Store retention for a given object.
     *
     * @param bucket    Bucket the object is stored in.
     * @param id        object ID to store tags for.
     * @param retention the retention.
     */
    public void storeRetention(BucketMetadata bucket, UUID id, Retention retention) {
        synchronized (lockStore.get(id)) {
            var s3ObjectMetadata = getS3ObjectMetadata(bucket, id);
            writeMetafile(bucket, new S3ObjectMetadata(
                    s3ObjectMetadata.getId(),
                    s3ObjectMetadata.getKey(),
                    s3ObjectMetadata.getSize(),
                    s3ObjectMetadata.getModificationDate(),
                    s3ObjectMetadata.getEtag(),
                    s3ObjectMetadata.getContentType(),
                    s3ObjectMetadata.getLastModified(),
                    s3ObjectMetadata.getDataPath(),
                    s3ObjectMetadata.getUserMetadata(),
                    s3ObjectMetadata.getTags(),
                    s3ObjectMetadata.getLegalHold(),
                    retention,
                    s3ObjectMetadata.getOwner(),
                    s3ObjectMetadata.getStoreHeaders(),
                    s3ObjectMetadata.getEncryptionHeaders(),
                    s3ObjectMetadata.getChecksumAlgorithm(),
                    s3ObjectMetadata.getChecksum(),
                    s3ObjectMetadata.getStorageClass()
            ));
        }
    }

    /**
     * Retrieves S3ObjectMetadata for a UUID of a key from a bucket.
     *
     * @param bucket Bucket from which to retrieve the object.
     * @param id     ID of the object key.
     * @return S3ObjectMetadata or null if not found
     */
    public S3ObjectMetadata getS3ObjectMetadata(BucketMetadata bucket, UUID id) {
        var metaPath = getMetaFilePath(bucket, id);

        if (Files.exists(metaPath)) {
            synchronized (lockStore.get(id)) {
                try {
                    return objectMapper.readValue(metaPath.toFile(), S3ObjectMetadata.class);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Could not read object metadata-file " + id, e);
                }
            }
        }
        return null;
    }

    /**
     * Copies an object to another bucket and encrypted object.
     *
     * @param sourceBucket      bucket to copy from.
     * @param sourceId          source object ID.
     * @param destinationBucket destination bucket.
     * @param destinationId     destination object ID.
     * @param destinationKey    destination object key.
     * @param userMetadata      User metadata to store for destination object
     * @return {@link CopyObjectResult} or null if source couldn't be found.
     */
    public CopyObjectResult copyS3Object(BucketMetadata sourceBucket,
                                         UUID sourceId,
                                         BucketMetadata destinationBucket,
                                         UUID destinationId,
                                         String destinationKey,
                                         Map<String, String> encryptionHeaders,
                                         Map<String, String> userMetadata) {
        var sourceObject = getS3ObjectMetadata(sourceBucket, sourceId);
        if (sourceObject == null) {
            return null;
        }
        synchronized (lockStore.get(sourceId)) {
            try (var inputStream = Files.newInputStream(sourceObject.getDataPath())) {
                var copiedObject = storeS3ObjectMetadata(destinationBucket,
                        destinationId,
                        destinationKey,
                        sourceObject.getContentType(),
                        sourceObject.getStoreHeaders(),
                        inputStream,
                        false,
                        userMetadata == null || userMetadata.isEmpty()
                                ? sourceObject.getUserMetadata() : userMetadata,
                        encryptionHeaders,
                        null,
                        sourceObject.getTags(),
                        sourceObject.getChecksumAlgorithm(),
                        sourceObject.getChecksum(),
                        sourceObject.getOwner(),
                        sourceObject.getStorageClass()
                );
                return new CopyObjectResult(copiedObject.getModificationDate(), copiedObject.getEtag());
            } catch (IOException e) {
                throw new IllegalStateException("Could not write object binary-file.", e);
            }
        }
    }

    /**
     * If source and destination is the same, pretend we copied - S3 does the same.
     * This does not change the modificationDate.
     * Also, this would need to increment the version if/when we support versioning.
     */
    public CopyObjectResult pretendToCopyS3Object(BucketMetadata sourceBucket,
                                                  UUID sourceId,
                                                  Map<String, String> userMetadata) {
        var sourceObject = getS3ObjectMetadata(sourceBucket, sourceId);
        if (sourceObject == null) {
            return null;
        }

        writeMetafile(sourceBucket, new S3ObjectMetadata(
                sourceObject.getId(),
                sourceObject.getKey(),
                sourceObject.getSize(),
                sourceObject.getModificationDate(),
                sourceObject.getEtag(),
                sourceObject.getContentType(),
                Instant.now().toEpochMilli(),
                sourceObject.getDataPath(),
                userMetadata == null || userMetadata.isEmpty()
                        ? sourceObject.getUserMetadata() : userMetadata,
                sourceObject.getTags(),
                sourceObject.getLegalHold(),
                sourceObject.getRetention(),
                sourceObject.getOwner(),
                sourceObject.getStoreHeaders(),
                sourceObject.getEncryptionHeaders(),
                sourceObject.getChecksumAlgorithm(),
                sourceObject.getChecksum(),
                sourceObject.getStorageClass()
        ));
        return new CopyObjectResult(sourceObject.getModificationDate(), sourceObject.getEtag());
    }

    /**
     * Removes an object key from a bucket.
     *
     * @param bucket bucket containing the object.
     * @param id     object to be deleted.
     * @return true if deletion succeeded.
     */
    public boolean deleteObject(BucketMetadata bucket, UUID id) {
        var s3ObjectMetadata = getS3ObjectMetadata(bucket, id);
        if (s3ObjectMetadata != null) {
            synchronized (lockStore.get(id)) {
                try {
                    FileUtils.deleteDirectory(getObjectFolderPath(bucket, id).toFile());
                } catch (IOException e) {
                    throw new IllegalStateException("Could not delete object-directory " + id, e);
                }
                lockStore.remove(id);
                return true;
            }
        } else {
            return false;
        }
    }

    void loadObjects(BucketMetadata bucketMetadata, Collection<UUID> ids) {
        var loaded = 0;
        for (var id : ids) {
            lockStore.putIfAbsent(id, new Object());
            var s3ObjectMetadata = getS3ObjectMetadata(bucketMetadata, id);
            if (s3ObjectMetadata != null) {
                loaded++;
            }
        }
        LOG.info("Loaded {}/{} objects for bucket {}", loaded, ids.size(), bucketMetadata.getName());
    }

    /**
     * Stores the content of an InputStream in a File.
     * Creates the File if it does not exist.
     *
     * @param inputStream the Stream to be saved.
     * @param filePath    Path where the stream should be saved.
     * @return the newly created File.
     */
    File inputStreamToFile(InputStream inputStream, Path filePath) {
        var targetFile = filePath.toFile();
        try {
            if (targetFile.createNewFile() && (!retainFilesOnExit)) {
                targetFile.deleteOnExit();
            }

            try (var is = inputStream;
                 var os = newOutputStream(targetFile.toPath())) {
                is.transferTo(os);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not write object binary-file.", e);
        }
        return targetFile;
    }

    InputStream wrapStream(InputStream dataStream, boolean useV4ChunkedWithSigningFormat,
                           boolean checksumEbedded) {
        if (useV4ChunkedWithSigningFormat && checksumEbedded) {
            return new AwsChunkedDecodingChecksumInputStream(dataStream);
        } else if (useV4ChunkedWithSigningFormat) {
            return new AwsChunkedDecodingInputStream(dataStream);
        } else if (checksumEbedded) {
            return new AwsChecksumInputStream(dataStream);
        } else {
            return dataStream;
        }
    }

    /**
     * Creates the root folder in which to store data and meta file.
     *
     * @param bucket the Bucket containing the Object.
     */
    private void createObjectRootFolder(BucketMetadata bucket, UUID id) {
        var objectRootFolder = getObjectFolderPath(bucket, id).toFile();
        if (objectRootFolder.mkdirs() && !retainFilesOnExit) {
            objectRootFolder.deleteOnExit();
        }
    }

    private Path getObjectFolderPath(BucketMetadata bucket, UUID id) {
        return Paths.get(bucket.getPath().toString(), id.toString());
    }

    private Path getMetaFilePath(BucketMetadata bucket, UUID id) {
        return Paths.get(getObjectFolderPath(bucket, id).toString(), META_FILE);
    }

    private Path getAclFilePath(BucketMetadata bucket, UUID id) {
        return Paths.get(getObjectFolderPath(bucket, id).toString(), ACL_FILE);
    }

    //TODO: should be private
    Path getDataFilePath(BucketMetadata bucket, UUID id) {
        return Paths.get(getObjectFolderPath(bucket, id).toString(), DATA_FILE);
    }

    private void writeMetafile(BucketMetadata bucket, S3ObjectMetadata s3ObjectMetadata) {
        var id = s3ObjectMetadata.getId();
        try {
            synchronized (lockStore.get(id)) {
                var metaFile = getMetaFilePath(bucket, id).toFile();
                if (!retainFilesOnExit) {
                    metaFile.deleteOnExit();
                }
                objectMapper.writeValue(metaFile, s3ObjectMetadata);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not write object metadata-file " + id, e);
        }
    }

    private AccessControlPolicy readAclFile(BucketMetadata bucket, UUID id) {
        try {
            synchronized (lockStore.get(id)) {
                var aclFile = getAclFilePath(bucket, id).toFile();
                if (!aclFile.exists()) {
                    return null;
                }
                var toDeserialize = FileUtils.readFileToString(aclFile, Charset.defaultCharset());
                return deserializeJaxb(toDeserialize);
            }
        } catch (IOException | JAXBException | XMLStreamException e) {
            throw new IllegalStateException("Could not read object acl-file " + id, e);
        }
    }

    private void writeAclFile(BucketMetadata bucket, UUID id, AccessControlPolicy policy) {
        try {
            synchronized (lockStore.get(id)) {
                var aclFile = getAclFilePath(bucket, id).toFile();
                if (!retainFilesOnExit) {
                    aclFile.deleteOnExit();
                }
                FileUtils.write(aclFile, serializeJaxb(policy), Charset.defaultCharset());
            }
        } catch (IOException | JAXBException e) {
            throw new IllegalStateException("Could not write object acl-file " + id, e);
        }
    }
}
