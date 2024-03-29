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

package cc.iotkit.s3mock;

import cc.iotkit.s3mock.dto.Tag;
import cc.iotkit.s3mock.util.AwsHttpHeaders;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Converts values of the {@link AwsHttpHeaders#X_AMZ_TAGGING} which is sent by the Amazon client.
 * Example: x-amz-tagging: tag1=value1&tag2=value2
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-tagging.html">API Reference</a>
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObject.html">API Reference</a>
 */
class TaggingHeaderConverter implements Converter<String, List<Tag>> {
  @Override
  @Nullable
  public List<Tag> convert(@NonNull String source) {
    var tags = new ArrayList<Tag>();
    String[] tagPairs = StringUtils.split(source, '&');
    for (String tag : tagPairs) {
      tags.add(new Tag(tag));
    }
    return tags.isEmpty() ? null : tags;
  }
}
