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

package cc.iotkit.s3mock.store;

import cc.iotkit.s3mock.dto.BucketLifecycleConfiguration;
import cc.iotkit.s3mock.dto.ObjectLockConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a bucket in S3, used to serialize and deserialize all metadata locally.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BucketMetadata {
    private String name;
    private String creationDate;
    private ObjectLockConfiguration objectLockConfiguration;
    private BucketLifecycleConfiguration bucketLifecycleConfiguration;
    private Path path;
    private Map<String, UUID> objects;

    public BucketMetadata(String name, String creationDate,
                          ObjectLockConfiguration objectLockConfiguration,
                          BucketLifecycleConfiguration bucketLifecycleConfiguration,
                          Path path) {
        this(name,
                creationDate,
                objectLockConfiguration,
                bucketLifecycleConfiguration,
                path,
                new HashMap<>());
    }

    public BucketMetadata withObjectLockConfiguration(
            ObjectLockConfiguration objectLockConfiguration) {
        return new BucketMetadata(getName(), getCreationDate(), objectLockConfiguration,
                getBucketLifecycleConfiguration(), getPath());
    }

    public BucketMetadata withBucketLifecycleConfiguration(
            BucketLifecycleConfiguration bucketLifecycleConfiguration) {
        return new BucketMetadata(getName(), getCreationDate(), getObjectLockConfiguration(),
                bucketLifecycleConfiguration, getPath());
    }

    public boolean doesKeyExist(String key) {
        return getID(key) != null;
    }

    public UUID addKey(String key) {
        if (doesKeyExist(key)) {
            return getID(key);
        } else {
            var uuid = UUID.randomUUID();
            this.objects.put(key, uuid);
            return uuid;
        }
    }

    public boolean removeKey(String key) {
        var removed = this.objects.remove(key);
        return removed != null;
    }

    public UUID getID(String key) {
        return this.objects.get(key);
    }
}
