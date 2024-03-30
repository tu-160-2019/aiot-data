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

package cc.iotkit.s3mock;

import static cc.iotkit.s3mock.util.AwsHttpHeaders.CONTENT_MD5;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.MetadataDirective.METADATA_DIRECTIVE_COPY;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.NOT_X_AMZ_COPY_SOURCE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.RANGE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_ACL;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_CONTENT_SHA256;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE_IF_MATCH;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE_IF_NONE_MATCH;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_DELETE_MARKER;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_METADATA_DIRECTIVE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_OBJECT_ATTRIBUTES;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_STORAGE_CLASS;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_TAGGING;
import static cc.iotkit.s3mock.util.AwsHttpParameters.ACL;
import static cc.iotkit.s3mock.util.AwsHttpParameters.ATTRIBUTES;
import static cc.iotkit.s3mock.util.AwsHttpParameters.DELETE;
import static cc.iotkit.s3mock.util.AwsHttpParameters.LEGAL_HOLD;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_ACL;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_ATTRIBUTES;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_LEGAL_HOLD;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_LIFECYCLE;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_RETENTION;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_TAGGING;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_UPLOADS;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_UPLOAD_ID;
import static cc.iotkit.s3mock.util.AwsHttpParameters.RETENTION;
import static cc.iotkit.s3mock.util.AwsHttpParameters.TAGGING;
import static cc.iotkit.s3mock.util.HeaderUtil.checksumAlgorithmFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.checksumFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.checksumHeaderFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.encryptionHeadersFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.isV4ChunkedWithSigningEnabled;
import static cc.iotkit.s3mock.util.HeaderUtil.mediaTypeFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.overrideHeadersFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.storeHeadersFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.userMetadataFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.userMetadataHeadersFrom;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.IF_MATCH;
import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;
import static org.springframework.http.HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import cc.iotkit.s3mock.service.ObjectService;
import cn.dev33.satoken.annotation.SaIgnore;
import cc.iotkit.s3mock.dto.AccessControlPolicy;
import cc.iotkit.s3mock.dto.CopyObjectResult;
import cc.iotkit.s3mock.dto.CopySource;
import cc.iotkit.s3mock.dto.Delete;
import cc.iotkit.s3mock.dto.DeleteResult;
import cc.iotkit.s3mock.dto.GetObjectAttributesOutput;
import cc.iotkit.s3mock.dto.LegalHold;
import cc.iotkit.s3mock.dto.ObjectAttributes;
import cc.iotkit.s3mock.dto.ObjectKey;
import cc.iotkit.s3mock.dto.Owner;
import cc.iotkit.s3mock.dto.Retention;
import cc.iotkit.s3mock.dto.StorageClass;
import cc.iotkit.s3mock.dto.Tag;
import cc.iotkit.s3mock.dto.TagSet;
import cc.iotkit.s3mock.dto.Tagging;
import cc.iotkit.s3mock.service.BucketService;
import cc.iotkit.s3mock.store.S3ObjectMetadata;
import cc.iotkit.s3mock.util.AwsHttpHeaders.MetadataDirective;
import cc.iotkit.s3mock.util.CannedAclUtil;
import cc.iotkit.s3mock.util.XmlUtil;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

/**
 * Handles requests related to objects.
 */
@SaIgnore
@CrossOrigin(originPatterns = "*", exposedHeaders = "*")
@Controller
@RequestMapping("${s3mock.contextPath:}")
public class ObjectController {
    private static final String RANGES_BYTES = "bytes";

    private final BucketService bucketService;
    private final ObjectService objectService;

    public ObjectController(BucketService bucketService, ObjectService objectService) {
        this.bucketService = bucketService;
        this.objectService = objectService;
    }

    //================================================================================================
    // /{bucketName:.+}
    //================================================================================================

    /**
     * This operation removes multiple objects.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjects.html">API Reference</a>
     *
     * @param bucketName name of bucket containing the object.
     * @param body       The delete request.
     * @return The {@link DeleteResult}
     */
    @PostMapping(
            value = {
                    //AWS SDK V2 pattern
                    "/{bucketName:.+}",
                    //AWS SDK V1 pattern
                    "/{bucketName:.+}/"
            },
            params = {
                    DELETE
            },
            produces = APPLICATION_XML_VALUE
    )
    public ResponseEntity<DeleteResult> deleteObjects(
            @PathVariable String bucketName,
            @RequestBody Delete body) {
        bucketService.verifyBucketExists(bucketName);
        //return version id
        return ResponseEntity.ok(objectService.deleteObjects(bucketName, body));
    }

    //================================================================================================
    // /{bucketName:.+}/{key}
    //================================================================================================

    /**
     * Retrieves metadata from an object without returning the object itself.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_HeadObject.html">API Reference</a>
     *
     * @param bucketName name of the bucket to look in
     * @return 200 with object metadata headers, 404 if not found.
     */
    @RequestMapping(
            value = "/{bucketName:.+}/{key:.*}",
            method = RequestMethod.HEAD
    )
    public ResponseEntity<Void> headObject(@PathVariable String bucketName,
                                           @PathVariable ObjectKey key,
                                           @RequestHeader(value = IF_MATCH, required = false) List<String> match,
                                           @RequestHeader(value = IF_NONE_MATCH, required = false) List<String> noneMatch) {
        //TODO: needs modified-since handling, see API
        bucketService.verifyBucketExists(bucketName);

        var s3ObjectMetadata = objectService.verifyObjectExists(bucketName, key.getKey());
        //return version id

        if (s3ObjectMetadata != null) {
            objectService.verifyObjectMatching(match, noneMatch, s3ObjectMetadata);
            return ResponseEntity.ok()
                    .eTag(s3ObjectMetadata.getEtag())
                    .header(HttpHeaders.ACCEPT_RANGES, RANGES_BYTES)
                    .headers(headers -> headers.setAll(s3ObjectMetadata.getStoreHeaders()))
                    .headers(headers -> headers.setAll(userMetadataHeadersFrom(s3ObjectMetadata)))
                    .headers(headers -> headers.setAll(s3ObjectMetadata.getEncryptionHeaders()))
                    .headers(h -> h.setAll(checksumHeaderFrom(s3ObjectMetadata)))
                    .header(X_AMZ_STORAGE_CLASS, s3ObjectMetadata.getStorageClass().toString())
                    .lastModified(s3ObjectMetadata.getLastModified())
                    .contentLength(Long.parseLong(s3ObjectMetadata.getSize()))
                    .contentType(mediaTypeFrom(s3ObjectMetadata.getContentType()))
                    .build();
        } else {
            return ResponseEntity.status(NOT_FOUND).build();
        }
    }

    /**
     * The DELETE operation removes an object.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObject.html">API Reference</a>
     *
     * @param bucketName name of bucket containing the object.
     * @return ResponseEntity with Status Code 204 if object was successfully deleted.
     */
    @DeleteMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    NOT_LIFECYCLE
            }
    )
    public ResponseEntity<Void> deleteObject(@PathVariable String bucketName,
                                             @PathVariable ObjectKey key) {
        bucketService.verifyBucketExists(bucketName);

        var deleted = objectService.deleteObject(bucketName, key.getKey());

        //return version id
        return ResponseEntity.noContent()
                .header(X_AMZ_DELETE_MARKER, String.valueOf(deleted))
                .build();
    }

    /**
     * Returns the File identified by bucketName and fileName.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObject.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     * @param range      byte range
     */
    @GetMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    NOT_UPLOADS,
                    NOT_UPLOAD_ID,
                    NOT_TAGGING,
                    NOT_LEGAL_HOLD,
                    NOT_RETENTION,
                    NOT_ACL,
                    NOT_ATTRIBUTES
            }
    )
    public ResponseEntity<StreamingResponseBody> getObject(@PathVariable String bucketName,
                                                           @PathVariable ObjectKey key,
                                                           @RequestHeader(value = RANGE, required = false) HttpRange range,
                                                           @RequestHeader(value = IF_MATCH, required = false) List<String> match,
                                                           @RequestHeader(value = IF_NONE_MATCH, required = false) List<String> noneMatch,
                                                           @RequestParam Map<String, String> queryParams) {
        //TODO: needs modified-since handling, see API
        bucketService.verifyBucketExists(bucketName);

        var s3ObjectMetadata = objectService.verifyObjectExists(bucketName, key.getKey());
        objectService.verifyObjectMatching(match, noneMatch, s3ObjectMetadata);

        if (range != null) {
            return getObjectWithRange(range, s3ObjectMetadata);
        }

        //return version id
        return ResponseEntity
                .ok()
                .eTag(s3ObjectMetadata.getEtag())
                .header(HttpHeaders.ACCEPT_RANGES, RANGES_BYTES)
                .headers(headers -> headers.setAll(s3ObjectMetadata.getStoreHeaders()))
                .headers(headers -> headers.setAll(userMetadataHeadersFrom(s3ObjectMetadata)))
                .headers(headers -> headers.setAll(s3ObjectMetadata.getEncryptionHeaders()))
                .headers(h -> h.setAll(checksumHeaderFrom(s3ObjectMetadata)))
                .header(X_AMZ_STORAGE_CLASS, s3ObjectMetadata.getStorageClass().toString())
                .lastModified(s3ObjectMetadata.getLastModified())
                .contentLength(Long.parseLong(s3ObjectMetadata.getSize()))
                .contentType(mediaTypeFrom(s3ObjectMetadata.getContentType()))
                .headers(headers -> headers.setAll(overrideHeadersFrom(queryParams)))
                .body(outputStream -> Files.copy(s3ObjectMetadata.getDataPath(), outputStream));
    }

    /**
     * Adds an ACL to an object.
     * This method accepts a String instead of the POJO. We need to use JAX-B annotations
     * instead of Jackson annotations because AWS decided to use xsi:type annotations in the XML
     * representation, which are not supported by Jackson.
     * It doesn't seem to be possible to use bot JAX-B and Jackson for (de-)serialization in parallel.
     * :-(
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectAcl.html">API Reference</a>
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/acl-overview.html">API Reference</a>
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/acl-overview.html#canned-acl">API Reference</a>
     *
     * @param bucketName the Bucket in which to store the file in.
     * @return {@link ResponseEntity} with Status Code and empty ETag.
     */
    @PutMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    ACL,
            },
            consumes = APPLICATION_XML_VALUE
    )
    public ResponseEntity<Void> putObjectAcl(@PathVariable final String bucketName,
                                             @PathVariable ObjectKey key,
                                             @RequestHeader(value = X_AMZ_ACL, required = false) ObjectCannedACL cannedAcl,
                                             @RequestBody(required = false) String body) throws XMLStreamException, JAXBException {
        bucketService.verifyBucketExists(bucketName);
        objectService.verifyObjectExists(bucketName, key.getKey());
        AccessControlPolicy policy;
        if (body != null) {
            policy = XmlUtil.deserializeJaxb(body);
        } else if (cannedAcl != null) {
            policy = CannedAclUtil.policyForCannedAcl(cannedAcl);
        } else {
            return ResponseEntity.badRequest().build();
        }
        objectService.setAcl(bucketName, key.getKey(), policy);
        return ResponseEntity
                .ok()
                .build();
    }

    /**
     * Gets ACL of an object.
     * This method returns a String instead of the POJO. We need to use JAX-B annotations
     * instead of Jackson annotations because AWS decided to use xsi:type annotations in the XML
     * representation, which are not supported by Jackson.
     * It doesn't seem to be possible to use bot JAX-B and Jackson for (de-)serialization in parallel.
     * :-(
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectAcl.html">API Reference</a>
     *
     * @param bucketName the Bucket in which to store the file in.
     * @return {@link ResponseEntity} with Status Code and empty ETag.
     */
    @GetMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    ACL,
            },
            produces = APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> getObjectAcl(@PathVariable final String bucketName,
                                               @PathVariable ObjectKey key) throws JAXBException {
        bucketService.verifyBucketExists(bucketName);
        objectService.verifyObjectExists(bucketName, key.getKey());
        var acl = objectService.getAcl(bucketName, key.getKey());
        return ResponseEntity.ok(XmlUtil.serializeJaxb(acl));
    }

    /**
     * Returns the tags identified by bucketName and fileName.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectTagging.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     */
    @GetMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    TAGGING
            },
            produces = {
                    APPLICATION_XML_VALUE,
                    APPLICATION_XML_VALUE + ";charset=UTF-8"
            }
    )
    public ResponseEntity<Tagging> getObjectTagging(@PathVariable String bucketName,
                                                    @PathVariable ObjectKey key) {
        bucketService.verifyBucketExists(bucketName);

        var s3ObjectMetadata = objectService.verifyObjectExists(bucketName, key.getKey());

        //return version id
        return ResponseEntity
                .ok()
                .eTag(s3ObjectMetadata.getEtag())
                .lastModified(s3ObjectMetadata.getLastModified())
                .body(new Tagging(new TagSet(s3ObjectMetadata.getTags())));
    }

    /**
     * Sets tags for a file identified by bucketName and fileName.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectTagging.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     * @param body       Tagging object
     */
    @PutMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    TAGGING
            },
            consumes = APPLICATION_XML_VALUE
    )
    public ResponseEntity<Void> putObjectTagging(@PathVariable String bucketName,
                                                 @PathVariable ObjectKey key,
                                                 @RequestBody Tagging body) {
        bucketService.verifyBucketExists(bucketName);

        var s3ObjectMetadata = objectService.verifyObjectExists(bucketName, key.getKey());
        objectService.setObjectTags(bucketName, key.getKey(), body.getTagSet().getTags());
        return ResponseEntity
                .ok()
                .eTag(s3ObjectMetadata.getEtag())
                .lastModified(s3ObjectMetadata.getLastModified())
                .build();
    }

    /**
     * Returns the legal hold for an object.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectLegalHold.html">API Reference</a>
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     */
    @GetMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    LEGAL_HOLD
            },
            produces = APPLICATION_XML_VALUE
    )
    public ResponseEntity<LegalHold> getLegalHold(@PathVariable String bucketName,
                                                  @PathVariable ObjectKey key) {
        bucketService.verifyBucketExists(bucketName);
        bucketService.verifyBucketObjectLockEnabled(bucketName);
        var s3ObjectMetadata = objectService.verifyObjectLockConfiguration(bucketName, key.getKey());

        return ResponseEntity
                .ok()
                .body(s3ObjectMetadata.getLegalHold());
    }

    /**
     * Sets legal hold for an object.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectLegalHold.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     * @param body       legal hold
     */
    @PutMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    LEGAL_HOLD
            },
            consumes = APPLICATION_XML_VALUE
    )
    public ResponseEntity<Void> putLegalHold(@PathVariable String bucketName,
                                             @PathVariable ObjectKey key,
                                             @RequestBody LegalHold body) {
        bucketService.verifyBucketExists(bucketName);
        bucketService.verifyBucketObjectLockEnabled(bucketName);

        objectService.verifyObjectExists(bucketName, key.getKey());
        objectService.setLegalHold(bucketName, key.getKey(), body);
        return ResponseEntity
                .ok()
                .build();
    }

    /**
     * Returns the retention for an object.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectRetention.html">API Reference</a>
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     */
    @GetMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    RETENTION
            },
            produces = APPLICATION_XML_VALUE
    )
    public ResponseEntity<Retention> getObjectRetention(@PathVariable String bucketName,
                                                        @PathVariable ObjectKey key) {
        bucketService.verifyBucketExists(bucketName);
        bucketService.verifyBucketObjectLockEnabled(bucketName);
        var s3ObjectMetadata = objectService.verifyObjectLockConfiguration(bucketName, key.getKey());

        return ResponseEntity
                .ok()
                .body(s3ObjectMetadata.getRetention());
    }

    /**
     * Sets retention for an object.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectRetention.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     * @param body       retention
     */
    @PutMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    RETENTION
            },
            consumes = APPLICATION_XML_VALUE
    )
    public ResponseEntity<Void> putObjectRetention(@PathVariable String bucketName,
                                                   @PathVariable ObjectKey key,
                                                   @RequestBody Retention body) {
        bucketService.verifyBucketExists(bucketName);
        bucketService.verifyBucketObjectLockEnabled(bucketName);

        objectService.verifyObjectExists(bucketName, key.getKey());
        objectService.verifyRetention(body);
        objectService.setRetention(bucketName, key.getKey(), body);
        return ResponseEntity
                .ok()
                .build();
    }

    /**
     * Returns the attributes for an object.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectAttributes.html">API Reference</a>
     *
     * @param bucketName The Bucket's name
     */
    @GetMapping(
            value = "/{bucketName:[a-z0-9.-]+}/{key:.*}",
            params = {
                    ATTRIBUTES
            },
            produces = APPLICATION_XML_VALUE
    )
    public ResponseEntity<GetObjectAttributesOutput> getObjectAttributes(
            @PathVariable String bucketName,
            @PathVariable ObjectKey key,
            @RequestHeader(value = IF_MATCH, required = false) List<String> match,
            @RequestHeader(value = IF_NONE_MATCH, required = false) List<String> noneMatch,
            @RequestHeader(value = X_AMZ_OBJECT_ATTRIBUTES) List<String> objectAttributes) {
        //TODO: needs modified-since handling, see API
        bucketService.verifyBucketExists(bucketName);

        //this is for either an object request, or a parts request.

        S3ObjectMetadata s3ObjectMetadata = objectService.verifyObjectExists(bucketName, key.getKey());
        objectService.verifyObjectMatching(match, noneMatch, s3ObjectMetadata);
        GetObjectAttributesOutput response = new GetObjectAttributesOutput(
                ObjectService.getChecksum(s3ObjectMetadata),
                objectAttributes.contains(ObjectAttributes.ETAG.toString())
                        ? s3ObjectMetadata.getEtag()
                        : null,
                null, //parts not supported right now
                objectAttributes.contains(ObjectAttributes.OBJECT_SIZE.toString())
                        ? Long.parseLong(s3ObjectMetadata.getSize())
                        : null,
                objectAttributes.contains(ObjectAttributes.STORAGE_CLASS.toString())
                        ? s3ObjectMetadata.getStorageClass()
                        : null
        );

        //return version id
        return ResponseEntity
                .ok()
                .lastModified(s3ObjectMetadata.getLastModified())
                .body(response);
    }


    /**
     * Adds an object to a bucket.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObject.html">API Reference</a>
     *
     * @param bucketName the Bucket in which to store the file in.
     * @return {@link ResponseEntity} with Status Code and empty ETag.
     */
    @PutMapping(
            value = "/{bucketName:.+}/{key:.*}",
            params = {
                    NOT_UPLOAD_ID,
                    NOT_TAGGING,
                    NOT_LEGAL_HOLD,
                    NOT_RETENTION,
                    NOT_ACL
            },
            headers = {
                    NOT_X_AMZ_COPY_SOURCE
            }
    )
    public ResponseEntity<Void> putObject(@PathVariable String bucketName,
                                          @PathVariable ObjectKey key,
                                          @RequestHeader(name = X_AMZ_TAGGING, required = false) List<Tag> tags,
                                          @RequestHeader(value = CONTENT_TYPE, required = false) String contentType,
                                          @RequestHeader(value = CONTENT_MD5, required = false) String contentMd5,
                                          @RequestHeader(value = X_AMZ_CONTENT_SHA256, required = false) String sha256Header,
                                          @RequestHeader(value = X_AMZ_STORAGE_CLASS, required = false, defaultValue = "STANDARD")
                                          StorageClass storageClass,
                                          @RequestHeader HttpHeaders httpHeaders,
                                          InputStream inputStream) {
        bucketService.verifyBucketExists(bucketName);

        var stream = objectService.verifyMd5(inputStream, contentMd5, sha256Header);
        //TODO: need to extract owner from headers
        var owner = Owner.DEFAULT_OWNER;
        var s3ObjectMetadata =
                objectService.putS3Object(bucketName,
                        key.getKey(),
                        mediaTypeFrom(contentType).toString(),
                        storeHeadersFrom(httpHeaders),
                        stream,
                        isV4ChunkedWithSigningEnabled(sha256Header),
                        userMetadataFrom(httpHeaders),
                        encryptionHeadersFrom(httpHeaders),
                        tags,
                        checksumAlgorithmFrom(httpHeaders),
                        checksumFrom(httpHeaders),
                        owner,
                        storageClass);

        //return version id
        return ResponseEntity
                .ok()
                .headers(h -> h.setAll(checksumHeaderFrom(s3ObjectMetadata)))
                .headers(h -> h.setAll(s3ObjectMetadata.getEncryptionHeaders()))
                .lastModified(s3ObjectMetadata.getLastModified())
                .eTag(s3ObjectMetadata.getEtag())
                .build();
    }

    /**
     * Copies an object to another bucket.
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_CopyObject.html">API Reference</a>
     *
     * @param bucketName name of the destination bucket
     * @param copySource path to source object
     * @return {@link CopyObjectResult}
     */
    @PutMapping(
            value = "/{bucketName:.+}/{key:.*}",
            headers = {
                    X_AMZ_COPY_SOURCE
            },
            params = {
                    NOT_UPLOAD_ID,
                    NOT_TAGGING,
                    NOT_LEGAL_HOLD,
                    NOT_RETENTION,
                    NOT_ACL
            },
            produces = APPLICATION_XML_VALUE
    )
    public ResponseEntity<CopyObjectResult> copyObject(@PathVariable String bucketName,
                                                       @PathVariable ObjectKey key,
                                                       @RequestHeader(value = X_AMZ_COPY_SOURCE) CopySource copySource,
                                                       @RequestHeader(value = X_AMZ_METADATA_DIRECTIVE,
                                                               defaultValue = METADATA_DIRECTIVE_COPY) MetadataDirective metadataDirective,
                                                       @RequestHeader(value = X_AMZ_COPY_SOURCE_IF_MATCH, required = false) List<String> match,
                                                       @RequestHeader(value = X_AMZ_COPY_SOURCE_IF_NONE_MATCH,
                                                               required = false) List<String> noneMatch,
                                                       @RequestHeader HttpHeaders httpHeaders) {
        //TODO: needs modified-since handling, see API

        bucketService.verifyBucketExists(bucketName);
        var s3ObjectMetadata = objectService.verifyObjectExists(copySource.getBucket(), copySource.getKey());
        objectService.verifyObjectMatchingForCopy(match, noneMatch, s3ObjectMetadata);

        Map<String, String> metadata = Collections.emptyMap();
        if (MetadataDirective.REPLACE == metadataDirective) {
            metadata = userMetadataFrom(httpHeaders);
        }

        //TODO: this is potentially illegal on S3. S3 throws a 400:
        // "This copy request is illegal because it is trying to copy an object to itself without
        // changing the object's metadata, storage class, website redirect location or encryption
        // attributes."

        var copyObjectResult = objectService.copyS3Object(copySource.getBucket(),
                copySource.getKey(),
                bucketName,
                key.getKey(),
                encryptionHeadersFrom(httpHeaders),
                metadata);

        //return version id / copy source version id
        //return expiration

        if (copyObjectResult == null) {
            return ResponseEntity
                    .notFound()
                    .headers(headers -> headers.setAll(s3ObjectMetadata.getEncryptionHeaders()))
                    .build();
        }
        return ResponseEntity
                .ok()
                .headers(headers -> headers.setAll(s3ObjectMetadata.getEncryptionHeaders()))
                .body(copyObjectResult);
    }

    /**
     * supports range different range ends. e.g. if content has 100 bytes, the range request could be:
     * bytes=10-100, 10--1 and 10-200
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObject.html">API Reference</a>
     *
     * @param range            {@link String}
     * @param s3ObjectMetadata {@link S3ObjectMetadata}
     */
    private ResponseEntity<StreamingResponseBody> getObjectWithRange(HttpRange range,
                                                                     S3ObjectMetadata s3ObjectMetadata) {
        var fileSize = s3ObjectMetadata.getDataPath().toFile().length();
        var bytesToRead = Math.min(fileSize - 1, range.getRangeEnd(fileSize))
                - range.getRangeStart(fileSize) + 1;

        if (bytesToRead < 0 || fileSize < range.getRangeStart(fileSize)) {
            return ResponseEntity.status(REQUESTED_RANGE_NOT_SATISFIABLE.value()).build();
        }

        return ResponseEntity
                .status(PARTIAL_CONTENT.value())
                .headers(headers -> headers.setAll(userMetadataHeadersFrom(s3ObjectMetadata)))
                .headers(headers -> headers.setAll(s3ObjectMetadata.getStoreHeaders()))
                .headers(headers -> headers.setAll(s3ObjectMetadata.getEncryptionHeaders()))
                .header(HttpHeaders.ACCEPT_RANGES, RANGES_BYTES)
                .header(HttpHeaders.CONTENT_RANGE,
                        String.format("bytes %s-%s/%s",
                                range.getRangeStart(fileSize), bytesToRead + range.getRangeStart(fileSize) - 1,
                                s3ObjectMetadata.getSize()))
                .eTag(s3ObjectMetadata.getEtag())
                .contentType(mediaTypeFrom(s3ObjectMetadata.getContentType()))
                .lastModified(s3ObjectMetadata.getLastModified())
                .contentLength(bytesToRead)
                .body(outputStream ->
                        extractBytesToOutputStream(range, s3ObjectMetadata, outputStream, fileSize, bytesToRead)
                );
    }

    private static void extractBytesToOutputStream(HttpRange range, S3ObjectMetadata s3ObjectMetadata,
                                                   OutputStream outputStream, long fileSize, long bytesToRead) throws IOException {
        try (var fis = Files.newInputStream(s3ObjectMetadata.getDataPath())) {
            var skip = fis.skip(range.getRangeStart(fileSize));
            if (skip == range.getRangeStart(fileSize)) {
                IOUtils.copy(new BoundedInputStream(fis, bytesToRead), outputStream);
            } else {
                throw new IllegalStateException("Could not skip exact byte range");
            }
        }
    }
}
