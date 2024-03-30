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

package cc.iotkit.s3mock.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * List-Parts result with some hard-coded values as this is sufficient for now.
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListParts.html">API Reference</a>
 */
@Data
@AllArgsConstructor
@JsonRootName("ListPartsResult")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ListPartsResult {
    @JsonProperty("Bucket")
    private String bucket;
    @JsonProperty("Key")
    private String key;
    @JsonProperty("UploadId")
    private String uploadId;
    @JsonProperty("PartNumberMarker")
    private String partNumberMarker;
    @JsonProperty("NextPartNumberMarker")
    private String nextPartNumberMarker;
    @JsonProperty("IsTruncated")
    private boolean truncated;
    @JsonProperty("StorageClass")
    private StorageClass storageClass;
    @JsonProperty("Part")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Part> parts;
    @JsonProperty("Owner")
    private Owner owner;
    @JsonProperty("Initiator")
    private Owner initiator;
    @JsonProperty("ChecksumAlgorithm")
    private ChecksumAlgorithm checksumAlgorithm;

    public ListPartsResult() {
        this.partNumberMarker = this.partNumberMarker == null ? "0" : this.partNumberMarker;
        this.nextPartNumberMarker = this.nextPartNumberMarker == null ? "1" : this.nextPartNumberMarker;
        this.storageClass = this.storageClass == null ? StorageClass.STANDARD : this.storageClass;
    }

    public ListPartsResult(String bucketName,
                           String key,
                           String uploadId,
                           List<Part> parts) {
        this(bucketName, key, uploadId, null, null, false, null, parts,
                null, null, null);
    }
}
