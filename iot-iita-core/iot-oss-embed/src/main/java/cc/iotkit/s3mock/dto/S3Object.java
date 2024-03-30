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

package cc.iotkit.s3mock.dto;

import static cc.iotkit.s3mock.util.EtagUtil.normalizeEtag;

import cc.iotkit.s3mock.store.S3ObjectMetadata;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class representing an Object on S3.
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_Object.html">API Reference</a>
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class S3Object{
    @JsonProperty("Key")
    String key;
    @JsonProperty("LastModified")
    String lastModified;
    @JsonProperty("ETag")
    String etag;
    @JsonProperty("Size")
    String size;
    @JsonProperty("StorageClass")
    StorageClass storageClass;
    @JsonProperty("Owner")
    Owner owner;
    @JsonProperty("ChecksumAlgorithm")
    ChecksumAlgorithm checksumAlgorithm;

  public S3Object() {
    etag = normalizeEtag(etag);
  }

  public static S3Object from(S3ObjectMetadata s3ObjectMetadata) {
    return new S3Object(s3ObjectMetadata.getKey(),
        s3ObjectMetadata.getModificationDate(),
        s3ObjectMetadata.getEtag(),
        s3ObjectMetadata.getSize(),
        s3ObjectMetadata.getStorageClass(),
        s3ObjectMetadata.getOwner(),
        s3ObjectMetadata.getChecksumAlgorithm());
  }
}
