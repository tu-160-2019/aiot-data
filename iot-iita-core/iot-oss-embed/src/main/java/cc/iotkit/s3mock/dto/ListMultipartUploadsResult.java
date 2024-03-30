/*
 *  Copyright 2017-2022 Adobe.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * List Multipart Uploads result.
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_ListMultipartUploads.html">API Reference</a>
 */
@Data
@AllArgsConstructor
@JsonRootName("ListMultipartUploadsResult")
public class ListMultipartUploadsResult {
    @JsonProperty("Bucket")
    private String bucket;
    @JsonProperty("KeyMarker")
    private String keyMarker;
    @JsonProperty("Delimiter")
    private String delimiter;
    @JsonProperty("Prefix")
    private String prefix;
    @JsonProperty("UploadIdMarker")
    private String uploadIdMarker;
    @JsonProperty("MaxUploads")
    private int maxUploads;
    @JsonProperty("IsTruncated")
    private boolean isTruncated;
    @JsonProperty("NextKeyMarker")
    private String nextKeyMarker;
    @JsonProperty("NextUploadIdMarker")
    private String nextUploadIdMarker;
    @JsonProperty("Upload")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<MultipartUpload> multipartUploads;
    @JsonProperty("CommonPrefixes")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Prefix> commonPrefixes;

}
