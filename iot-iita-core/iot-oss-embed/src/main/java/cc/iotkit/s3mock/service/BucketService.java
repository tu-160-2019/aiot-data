/*
 *  Copyright 2017-2023 Adobe.
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

package cc.iotkit.s3mock.service;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static software.amazon.awssdk.utils.http.SdkHttpUtils.urlEncodeIgnoreSlashes;

import cc.iotkit.s3mock.S3Exception;
import cc.iotkit.s3mock.dto.*;
import cc.iotkit.s3mock.dto.Bucket;
import cc.iotkit.s3mock.dto.BucketLifecycleConfiguration;
import cc.iotkit.s3mock.dto.Buckets;
import cc.iotkit.s3mock.dto.ListAllMyBucketsResult;
import cc.iotkit.s3mock.dto.ListBucketResult;
import cc.iotkit.s3mock.dto.ListBucketResultV2;
import cc.iotkit.s3mock.dto.ListVersionsResult;
import cc.iotkit.s3mock.dto.ObjectLockConfiguration;
import cc.iotkit.s3mock.dto.ObjectVersion;
import cc.iotkit.s3mock.dto.Prefix;
import cc.iotkit.s3mock.dto.S3Object;
import cc.iotkit.s3mock.store.BucketStore;
import cc.iotkit.s3mock.store.ObjectStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import software.amazon.awssdk.utils.http.SdkHttpUtils;

import javax.annotation.PostConstruct;

public class BucketService {
    private final Map<String, String> listObjectsPagingStateCache = new ConcurrentHashMap<>();
    private final BucketStore bucketStore;
    private final ObjectStore objectStore;

    public BucketService(BucketStore bucketStore, ObjectStore objectStore) {
        this.bucketStore = bucketStore;
        this.objectStore = objectStore;
    }

    @PostConstruct
    public void init() {
//        createBucket("bucket1", false);
    }

    public boolean isBucketEmpty(String bucketName) {
        return bucketStore.isBucketEmpty(bucketName);
    }

    public boolean doesBucketExist(String bucketName) {
        return bucketStore.doesBucketExist(bucketName);
    }

    public ListAllMyBucketsResult listBuckets() {
        var buckets = bucketStore
                .listBuckets()
                .stream()
                .filter(Objects::nonNull)
                .map(Bucket::from)
                .collect(Collectors.toList());
        return new ListAllMyBucketsResult(Owner.DEFAULT_OWNER, new Buckets(buckets));
    }

    /**
     * Retrieves a Bucket identified by its name.
     *
     * @param bucketName of the Bucket to be retrieved
     * @return the Bucket or null if not found
     */
    public Bucket getBucket(String bucketName) {
        return Bucket.from(bucketStore.getBucketMetadata(bucketName));
    }

    /**
     * Creates a Bucket identified by its name.
     *
     * @param bucketName of the Bucket to be created
     * @return the Bucket
     */
    public Bucket createBucket(String bucketName, boolean objectLockEnabled) {
        return Bucket.from(bucketStore.createBucket(bucketName, objectLockEnabled));
    }

    public boolean deleteBucket(String bucketName) {
        return bucketStore.deleteBucket(bucketName);
    }

    public void setObjectLockConfiguration(String bucketName, ObjectLockConfiguration configuration) {
        var bucketMetadata = bucketStore.getBucketMetadata(bucketName);
        bucketStore.storeObjectLockConfiguration(bucketMetadata, configuration);
    }

    public ObjectLockConfiguration getObjectLockConfiguration(String bucketName) {
        var bucketMetadata = bucketStore.getBucketMetadata(bucketName);
        var objectLockConfiguration = bucketMetadata.getObjectLockConfiguration();
        if (objectLockConfiguration != null) {
            return objectLockConfiguration;
        } else {
            throw S3Exception.NOT_FOUND_BUCKET_OBJECT_LOCK;
        }
    }

    public void setBucketLifecycleConfiguration(String bucketName,
                                                BucketLifecycleConfiguration configuration) {
        var bucketMetadata = bucketStore.getBucketMetadata(bucketName);
        bucketStore.storeBucketLifecycleConfiguration(bucketMetadata, configuration);
    }

    public void deleteBucketLifecycleConfiguration(String bucketName) {
        setBucketLifecycleConfiguration(bucketName, null);
    }

    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String bucketName) {
        var bucketMetadata = bucketStore.getBucketMetadata(bucketName);
        var configuration = bucketMetadata.getBucketLifecycleConfiguration();
        if (configuration != null) {
            return configuration;
        } else {
            throw S3Exception.NO_SUCH_LIFECYCLE_CONFIGURATION;
        }
    }

    /**
     * Retrieves S3Objects from a bucket.
     *
     * @param bucketName the Bucket in which to list the file(s) in.
     * @param prefix     {@link String} object file name starts with
     * @return S3Objects found in bucket for the given prefix
     */
    public List<S3Object> getS3Objects(String bucketName, String prefix) {
        var bucketMetadata = bucketStore.getBucketMetadata(bucketName);
        var uuids = bucketStore.lookupKeysInBucket(prefix, bucketName);
        return uuids
                .stream()
                .map(uuid -> objectStore.getS3ObjectMetadata(bucketMetadata, uuid))
                .filter(Objects::nonNull)
                .map(S3Object::from)
                // List Objects results are expected to be sorted by key
                .sorted(Comparator.comparing(S3Object::getKey))
                .collect(Collectors.toList());
    }

    public ListVersionsResult listVersions(String bucketName,
                                           String prefix,
                                           String delimiter,
                                           String encodingType,
                                           String startAfter,
                                           Integer maxKeys,
                                           String continuationToken,
                                           String keyMarker,
                                           String versionIdMarker) {
        //first implementation with dummy versions, just list objects for now.
        ListBucketResultV2 result = listObjectsV2(bucketName, prefix, delimiter, encodingType,
                startAfter, maxKeys, continuationToken);

        var versions = result.getContents().stream().map(ObjectVersion::from).collect(Collectors.toList());

        return new ListVersionsResult(result.getName(),
                result.getPrefix(),
                result.getMaxKeys(),
                result.isTruncated(),
                result.getCommonPrefixes(),
                delimiter,
                result.getEncodingType(),
                result.getContinuationToken(),
                null,
                result.getNextContinuationToken(),
                null,
                versions,
                null);
    }

    public ListBucketResultV2 listObjectsV2(String bucketName,
                                            String prefix,
                                            String delimiter,
                                            String encodingType,
                                            String startAfter,
                                            Integer maxKeys,
                                            String continuationToken) {

        var contents = getS3Objects(bucketName, prefix);
        var nextContinuationToken = (String) null;
        var isTruncated = false;

    /*
      Start-after is valid only in first request.
      If the response is truncated,
      you can specify this parameter along with the continuation-token parameter,
      and then Amazon S3 ignores this parameter.
     */
        if (continuationToken != null) {
            var continueAfter = listObjectsPagingStateCache.get(continuationToken);
            contents = filterObjectsBy(contents, continueAfter);
            listObjectsPagingStateCache.remove(continuationToken);
        } else {
            contents = filterObjectsBy(contents, startAfter);
        }

        var commonPrefixes = collapseCommonPrefixes(prefix, delimiter, contents);
        contents = filterObjectsBy(contents, commonPrefixes);

        if (contents.size() > maxKeys) {
            isTruncated = true;
            nextContinuationToken = UUID.randomUUID().toString();
            contents = contents.subList(0, maxKeys);
            listObjectsPagingStateCache.put(nextContinuationToken,
                    contents.get(maxKeys - 1).getKey());
        }

        var returnPrefix = prefix;
        var returnStartAfter = startAfter;
        var returnCommonPrefixes = commonPrefixes;

        if (Objects.equals("url", encodingType)) {
            contents = apply(contents, object -> new S3Object(urlEncodeIgnoreSlashes(object.getKey()),
                    object.getLastModified(),
                    object.getEtag(),
                    object.getSize(),
                    object.getStorageClass(),
                    object.getOwner(),
                    object.getChecksumAlgorithm()));
            returnPrefix = urlEncodeIgnoreSlashes(prefix);
            returnStartAfter = urlEncodeIgnoreSlashes(startAfter);
            returnCommonPrefixes = apply(commonPrefixes, SdkHttpUtils::urlEncodeIgnoreSlashes);
        }

        return new ListBucketResultV2(bucketName, returnPrefix, maxKeys,
                isTruncated, contents, returnCommonPrefixes.stream().map(Prefix::new).collect(Collectors.toList()),
                continuationToken, String.valueOf(contents.size()),
                nextContinuationToken, returnStartAfter, encodingType);
    }

    @Deprecated(since = "2.12.2", forRemoval = true)
    public ListBucketResult listObjectsV1(String bucketName, String prefix, String delimiter,
                                          String marker, String encodingType, Integer maxKeys) {

        verifyMaxKeys(maxKeys);
        verifyEncodingType(encodingType);

        var contents = getS3Objects(bucketName, prefix);
        contents = filterObjectsBy(contents, marker);

        var isTruncated = false;
        var nextMarker = (String) null;

        var commonPrefixes = collapseCommonPrefixes(prefix, delimiter, contents);
        contents = filterObjectsBy(contents, commonPrefixes);
        if (maxKeys < contents.size()) {
            contents = contents.subList(0, maxKeys);
            isTruncated = true;
            if (maxKeys > 0) {
                nextMarker = contents.get(maxKeys - 1).getKey();
            }
        }

        var returnPrefix = prefix;
        var returnCommonPrefixes = commonPrefixes;

        if (Objects.equals("url", encodingType)) {
            contents = apply(contents, object -> new S3Object(urlEncodeIgnoreSlashes(object.getKey()),
                    object.getLastModified(),
                    object.getEtag(),
                    object.getSize(),
                    object.getStorageClass(),
                    object.getOwner(),
                    object.getChecksumAlgorithm()));
            returnPrefix = urlEncodeIgnoreSlashes(prefix);
            returnCommonPrefixes = apply(commonPrefixes, SdkHttpUtils::urlEncodeIgnoreSlashes);
        }

        return new ListBucketResult(bucketName, returnPrefix, marker, maxKeys, isTruncated,
                encodingType, nextMarker, contents,
                returnCommonPrefixes.stream().map(Prefix::new).collect(Collectors.toList()));
    }

    public void verifyBucketExists(String bucketName) {
        if (!bucketStore.doesBucketExist(bucketName)) {
            throw S3Exception.NO_SUCH_BUCKET;
        }
    }

    public void verifyBucketObjectLockEnabled(String bucketName) {
        if (!bucketStore.isObjectLockEnabled(bucketName)) {
            throw S3Exception.NOT_FOUND_BUCKET_OBJECT_LOCK;
        }
    }

    /**
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html">API Reference Bucket Naming</a>.
     */
    public void verifyBucketNameIsAllowed(String bucketName) {
        if (!bucketName.matches("[a-z0-9.-]+")) {
            throw S3Exception.INVALID_BUCKET_NAME;
        }
    }

    public void verifyBucketIsEmpty(String bucketName) {
        if (!bucketStore.isBucketEmpty(bucketName)) {
            throw S3Exception.BUCKET_NOT_EMPTY;
        }
    }

    public void verifyBucketDoesNotExist(String bucketName) {
        if (bucketStore.doesBucketExist(bucketName)) {
            //currently, all buckets have the same owner in S3Mock. If the bucket exists, it's owned by
            //the owner that tries to create the bucket owns the existing bucket too.
            throw S3Exception.BUCKET_ALREADY_OWNED_BY_YOU;
        }
    }

    public void verifyMaxKeys(Integer maxKeys) {
        if (maxKeys < 0) {
            throw S3Exception.INVALID_REQUEST_MAXKEYS;
        }
    }

    public void verifyEncodingType(String encodingType) {
        if (isNotEmpty(encodingType) && !"url".equals(encodingType)) {
            throw S3Exception.INVALID_REQUEST_ENCODINGTYPE;
        }
    }


    /**
     * Collapse all bucket elements with keys starting with some prefix up to the given delimiter into
     * one prefix entry. Collapsed elements are removed from the contents list.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListObjectsV2.html">API Reference</a>
     *
     * @param queryPrefix the key prefix as specified in the list request
     * @param delimiter   the delimiter used to separate a prefix from the rest of the object name
     * @param s3Objects   the list of objects to use for collapsing the prefixes
     */
    static List<String> collapseCommonPrefixes(String queryPrefix, String delimiter,
                                               List<S3Object> s3Objects) {
        var commonPrefixes = new ArrayList<String>();
        if (isEmpty(delimiter)) {
            return commonPrefixes;
        }

        var normalizedQueryPrefix = queryPrefix == null ? "" : queryPrefix;

        for (var c : s3Objects) {
            var key = c.getKey();
            if (key.startsWith(normalizedQueryPrefix)) {
                int delimiterIndex = key.indexOf(delimiter, normalizedQueryPrefix.length());
                if (delimiterIndex > 0) {
                    var commonPrefix = key.substring(0, delimiterIndex + delimiter.length());
                    if (!commonPrefixes.contains(commonPrefix)) {
                        commonPrefixes.add(commonPrefix);
                    }
                }
            }
        }
        return commonPrefixes;
    }

    private static <T> List<T> apply(List<T> contents, UnaryOperator<T> extractor) {
        return contents
                .stream()
                .map(extractor)
                .collect(Collectors.toList());
    }

    static List<S3Object> filterObjectsBy(List<S3Object> s3Objects,
                                          String startAfter) {
        if (isNotEmpty(startAfter)) {
            return s3Objects
                    .stream()
                    .filter(p -> p.getKey().compareTo(startAfter) > 0)
                    .collect(Collectors.toList());
        } else {
            return s3Objects;
        }
    }

    static List<S3Object> filterObjectsBy(List<S3Object> s3Objects,
                                          List<String> commonPrefixes) {
        if (commonPrefixes != null && !commonPrefixes.isEmpty()) {
            return s3Objects
                    .stream()
                    .filter(c -> commonPrefixes
                            .stream()
                            .noneMatch(p -> c.getKey().startsWith(p))
                    )
                    .collect(Collectors.toList());
        } else {
            return s3Objects;
        }
    }
}
