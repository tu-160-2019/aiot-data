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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeletedObject.html">API Reference</a>.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeletedS3Object{
    @JsonProperty("Key")
    private String key;
    @JsonProperty("VersionId")
    private String versionId;
    @JsonProperty("DeleteMarker")
    private Boolean deleteMarker;
    @JsonProperty("DeleteMarkerVersionId")
    private String deleteMarkerVersionId;

  public static DeletedS3Object from(S3ObjectIdentifier s3ObjectIdentifier) {
    return new DeletedS3Object(
        s3ObjectIdentifier.getKey(),
        s3ObjectIdentifier.getVersionId(),
        null,
        null
    );
  }
}
