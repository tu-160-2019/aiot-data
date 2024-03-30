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

import static cc.iotkit.s3mock.util.EtagUtil.normalizeEtag;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_Part.html">API Reference</a>.
 */
@Data
@AllArgsConstructor
public class Part {
    @JsonProperty("PartNumber")
    private Integer partNumber;
    @JsonProperty("ETag")
    private String etag;
    @JsonProperty("LastModified")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date lastModified;
    @JsonProperty("Size")
    private Long size;

    public Part() {
        etag = normalizeEtag(etag);
    }
}
