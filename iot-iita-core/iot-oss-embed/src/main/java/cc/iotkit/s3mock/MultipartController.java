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

import static cc.iotkit.s3mock.util.AwsHttpHeaders.NOT_X_AMZ_COPY_SOURCE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.NOT_X_AMZ_COPY_SOURCE_RANGE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_CONTENT_SHA256;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE_IF_MATCH;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE_IF_NONE_MATCH;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_COPY_SOURCE_RANGE;
import static cc.iotkit.s3mock.util.AwsHttpHeaders.X_AMZ_STORAGE_CLASS;
import static cc.iotkit.s3mock.util.AwsHttpParameters.NOT_LIFECYCLE;
import static cc.iotkit.s3mock.util.AwsHttpParameters.PART_NUMBER;
import static cc.iotkit.s3mock.util.AwsHttpParameters.UPLOADS;
import static cc.iotkit.s3mock.util.AwsHttpParameters.UPLOAD_ID;
import static cc.iotkit.s3mock.util.HeaderUtil.checksumAlgorithmFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.checksumFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.encryptionHeadersFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.isV4ChunkedWithSigningEnabled;
import static cc.iotkit.s3mock.util.HeaderUtil.storeHeadersFrom;
import static cc.iotkit.s3mock.util.HeaderUtil.userMetadataFrom;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import cc.iotkit.s3mock.dto.*;
import cc.iotkit.s3mock.dto.CompleteMultipartUpload;
import cc.iotkit.s3mock.dto.CompleteMultipartUploadResult;
import cc.iotkit.s3mock.dto.CopyPartResult;
import cc.iotkit.s3mock.dto.CopySource;
import cc.iotkit.s3mock.dto.InitiateMultipartUploadResult;
import cc.iotkit.s3mock.dto.ListMultipartUploadsResult;
import cc.iotkit.s3mock.dto.ListPartsResult;
import cc.iotkit.s3mock.dto.ObjectKey;
import cc.iotkit.s3mock.dto.StorageClass;
import cc.iotkit.s3mock.service.BucketService;
import cc.iotkit.s3mock.service.MultipartService;
import cc.iotkit.s3mock.service.ObjectService;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests related to parts.
 */
@CrossOrigin(originPatterns = "*", exposedHeaders = "*")
@Controller
@RequestMapping("${s3mock.contextPath:}")
public class MultipartController {

  private final BucketService bucketService;
  private final ObjectService objectService;
  private final MultipartService multipartService;

  public MultipartController(BucketService bucketService, ObjectService objectService,
      MultipartService multipartService) {
    this.bucketService = bucketService;
    this.objectService = objectService;
    this.multipartService = multipartService;
  }

  //================================================================================================
  // /{bucketName:.+}
  //================================================================================================

  /**
   * Lists all in-progress multipart uploads.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListMultipartUploads.html">API Reference</a>
   *
   * <p>Not yet supported request parameters: delimiter, encoding-type, max-uploads, key-marker,
   * upload-id-marker.</p>
   *
   * @param bucketName the Bucket in which to store the file in.
   *
   * @return the {@link ListMultipartUploadsResult}
   */
  @GetMapping(
      value = {
          //AWS SDK V2 pattern
          "/{bucketName:.+}",
          //AWS SDK V1 pattern
          "/{bucketName:.+}/"
      },
      params = {
          UPLOADS
      },
      produces = APPLICATION_XML_VALUE
  )
  public ResponseEntity<ListMultipartUploadsResult> listMultipartUploads(
      @PathVariable String bucketName,
      @RequestParam(required = false) String prefix) {
    bucketService.verifyBucketExists(bucketName);

    return ResponseEntity.ok(multipartService.listMultipartUploads(bucketName, prefix));
  }

  //================================================================================================
  // /{bucketName:.+}/{key}
  //================================================================================================

  /**
   * Aborts a multipart upload for a given uploadId.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_AbortMultipartUpload.html">API Reference</a>
   *
   * @param bucketName the Bucket in which to store the file in.
   * @param uploadId id of the upload. Has to match all other part's uploads.
   */
  @DeleteMapping(
      value = "/{bucketName:.+}/{key:.*}",
      params = {
          UPLOAD_ID,
          NOT_LIFECYCLE
      },
      produces = APPLICATION_XML_VALUE
  )
  public ResponseEntity<Void> abortMultipartUpload(@PathVariable String bucketName,
      @PathVariable ObjectKey key,
      @RequestParam String uploadId) {
    bucketService.verifyBucketExists(bucketName);
    multipartService.verifyMultipartUploadExists(uploadId);
    multipartService.abortMultipartUpload(bucketName, key.getKey(), uploadId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Lists all parts a file multipart upload.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListParts.html">API Reference</a>
   *
   * @param bucketName the Bucket in which to store the file in.
   * @param uploadId id of the upload. Has to match all other part's uploads.
   *
   * @return the {@link ListPartsResult}
   */
  @GetMapping(
      value = "/{bucketName:.+}/{key:.*}",
      params = {
          UPLOAD_ID
      },
      produces = APPLICATION_XML_VALUE
  )
  public ResponseEntity<ListPartsResult> listParts(@PathVariable String bucketName,
      @PathVariable ObjectKey key,
      @RequestParam String uploadId) {
    bucketService.verifyBucketExists(bucketName);
    multipartService.verifyMultipartUploadExists(uploadId);

    return ResponseEntity
        .ok(multipartService.getMultipartUploadParts(bucketName, key.getKey(), uploadId));
  }


  /**
   * Adds an object to a bucket accepting encryption headers.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_UploadPart.html">API Reference</a>
   *
   * @param bucketName the Bucket in which to store the file in.
   * @param uploadId id of the upload. Has to match all other part's uploads.
   * @param partNumber number of the part to upload.
   *
   * @return the etag of the uploaded part.
   *
   */
  @PutMapping(
      value = "/{bucketName:.+}/{key:.*}",
      params = {
          UPLOAD_ID,
          PART_NUMBER
      },
      headers = {
          NOT_X_AMZ_COPY_SOURCE,
          NOT_X_AMZ_COPY_SOURCE_RANGE
      }
  )
  public ResponseEntity<Void> uploadPart(@PathVariable String bucketName,
      @PathVariable ObjectKey key,
      @RequestParam String uploadId,
      @RequestParam String partNumber,
      @RequestHeader(value = X_AMZ_CONTENT_SHA256, required = false) String sha256Header,
      @RequestHeader HttpHeaders httpHeaders,
      InputStream inputStream) {
    bucketService.verifyBucketExists(bucketName);
    multipartService.verifyMultipartUploadExists(uploadId);
    multipartService.verifyPartNumberLimits(partNumber);

    var checksum = checksumFrom(httpHeaders);
    var checksumAlgorithm = checksumAlgorithmFrom(httpHeaders);

    //persist checksum per part
    var etag = multipartService.putPart(bucketName,
        key.getKey(),
        uploadId,
        partNumber,
        inputStream,
        isV4ChunkedWithSigningEnabled(sha256Header),
        encryptionHeadersFrom(httpHeaders));

    //return checksum headers
    //return encryption headers
    return ResponseEntity.ok().eTag("\"" + etag + "\"").build();
  }

  /**
   * Uploads a part by copying data from an existing object as data source.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_UploadPartCopy.html">API Reference</a>
   *
   * @param copySource References the Objects to be copied.
   * @param copyRange Defines the byte range for this part. Optional.
   * @param uploadId id of the upload. Has to match all other part's uploads.
   * @param partNumber number of the part to upload.
   *
   * @return The etag of the uploaded part.
   *
   */
  @PutMapping(
      value = "/{bucketName:.+}/{key:.*}",
      headers = {
          X_AMZ_COPY_SOURCE,
      },
      params = {
          UPLOAD_ID,
          PART_NUMBER
      },
      produces = APPLICATION_XML_VALUE)
  public ResponseEntity<CopyPartResult> uploadPartCopy(
      @PathVariable String bucketName,
      @PathVariable ObjectKey key,
      @RequestHeader(value = X_AMZ_COPY_SOURCE) CopySource copySource,
      @RequestHeader(value = X_AMZ_COPY_SOURCE_RANGE, required = false) HttpRange copyRange,
      @RequestHeader(value = X_AMZ_COPY_SOURCE_IF_MATCH, required = false) List<String> match,
      @RequestHeader(value = X_AMZ_COPY_SOURCE_IF_NONE_MATCH,
          required = false) List<String> noneMatch,
      @RequestParam String uploadId,
      @RequestParam String partNumber,
      @RequestHeader HttpHeaders httpHeaders) {
    //TODO: needs modified-since handling, see API
    bucketService.verifyBucketExists(bucketName);
    multipartService.verifyPartNumberLimits(partNumber);
    var s3ObjectMetadata = objectService.verifyObjectExists(copySource.getBucket(), copySource.getKey());
    objectService.verifyObjectMatchingForCopy(match, noneMatch, s3ObjectMetadata);

    var result = multipartService.copyPart(copySource.getBucket(),
        copySource.getKey(),
        copyRange,
        partNumber,
        bucketName,
        key.getKey(),
        uploadId,
        encryptionHeadersFrom(httpHeaders)
    );

    //return encryption headers
    //return source version id
    return ResponseEntity.ok(result);
  }

  /**
   * Initiates a multipart upload accepting encryption headers.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_CreateMultipartUpload.html">API Reference</a>
   *
   * @param bucketName the Bucket in which to store the file in.
   *
   * @return the {@link InitiateMultipartUploadResult}.
   */
  @PostMapping(
      value = "/{bucketName:.+}/{key:.*}",
      params = {
          UPLOADS
      },
      produces = APPLICATION_XML_VALUE)
  public ResponseEntity<InitiateMultipartUploadResult> createMultipartUpload(
      @PathVariable String bucketName,
      @PathVariable ObjectKey key,
      @RequestHeader(value = CONTENT_TYPE, required = false) String contentType,
      @RequestHeader(value = X_AMZ_STORAGE_CLASS, required = false, defaultValue = "STANDARD")
      StorageClass storageClass,
      @RequestHeader HttpHeaders httpHeaders) {
    bucketService.verifyBucketExists(bucketName);

    var checksum = checksumFrom(httpHeaders);
    var checksumAlgorithm = checksumAlgorithmFrom(httpHeaders);

    var uploadId = UUID.randomUUID().toString();
    var result =
        multipartService.prepareMultipartUpload(bucketName,
            key.getKey(),
            contentType,
            storeHeadersFrom(httpHeaders),
            uploadId,
            Owner.DEFAULT_OWNER,
            Owner.DEFAULT_OWNER,
            userMetadataFrom(httpHeaders),
            encryptionHeadersFrom(httpHeaders),
            storageClass,
            checksum,
            checksumAlgorithm);

    //return encryption headers
    //return checksum algorithm headers
    return ResponseEntity.ok(result);
  }

  /**
   * Adds an object to a bucket.
   * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_CompleteMultipartUpload.html">API Reference</a>
   *
   * @param bucketName the Bucket in which to store the file in.
   * @param uploadId id of the upload. Has to match all other part's uploads.
   *
   * @return {@link CompleteMultipartUploadResult}
   */
  @PostMapping(
      value = "/{bucketName:.+}/{key:.*}",
      params = {
          UPLOAD_ID
      },
      produces = APPLICATION_XML_VALUE)
  public ResponseEntity<CompleteMultipartUploadResult> completeMultipartUpload(
      @PathVariable String bucketName,
      @PathVariable ObjectKey key,
      @RequestParam String uploadId,
      @RequestBody CompleteMultipartUpload upload,
      HttpServletRequest request,
      @RequestHeader HttpHeaders httpHeaders) {
    bucketService.verifyBucketExists(bucketName);
    multipartService.verifyMultipartUploadExists(uploadId);
    multipartService.verifyMultipartParts(bucketName, key.getKey(), uploadId, upload.getParts());
    var objectName = key.getKey();
    var locationWithEncodedKey = request
        .getRequestURL()
        .toString()
        .replace(objectName, SdkHttpUtils.urlEncode(objectName));

    var result = multipartService.completeMultipartUpload(bucketName,
        key.getKey(),
        uploadId,
        upload.getParts(),
        encryptionHeadersFrom(httpHeaders),
        locationWithEncodedKey);

    //return checksum and encryption headers.
    //return version id
    return ResponseEntity.ok(result);
  }
}
