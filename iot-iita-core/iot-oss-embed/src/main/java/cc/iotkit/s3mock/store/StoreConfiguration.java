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

import static cc.iotkit.s3mock.store.BucketStore.BUCKET_META_FILE;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StoreProperties.class)
public class StoreConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(StoreConfiguration.class);
    static final DateTimeFormatter S3_OBJECT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    @Bean
    ObjectStore objectStore(StoreProperties properties, List<String> bucketNames,
                            BucketStore bucketStore, ObjectMapper objectMapper) {
        var objectStore = new ObjectStore(properties.isRetainFilesOnExit(),
                S3_OBJECT_DATE_FORMAT, objectMapper);
        for (var bucketName : bucketNames) {
            var bucketMetadata = bucketStore.getBucketMetadata(bucketName);
            if (bucketMetadata != null) {
                objectStore.loadObjects(bucketMetadata, bucketMetadata.getObjects().values());
            }
        }
        return objectStore;
    }

    @Bean
    BucketStore bucketStore(StoreProperties properties, File rootFolder, List<String> bucketNames,
                            ObjectMapper objectMapper) {
        var bucketStore = new BucketStore(rootFolder, properties.isRetainFilesOnExit(),
                S3_OBJECT_DATE_FORMAT, objectMapper);
        //load existing buckets first
        bucketStore.loadBuckets(bucketNames);

        //load initialBuckets if not part of existing buckets
        properties
                .getInitialBuckets()
                .stream()
                .filter(name -> {
                    boolean partOfExistingBuckets = bucketNames.contains(name);
                    if (partOfExistingBuckets) {
                        LOG.info("Skip initial bucket {}, it's part of the existing buckets.", name);
                    }
                    return !partOfExistingBuckets;
                })
                .forEach(name -> {
                    bucketStore.createBucket(name, false);
                    LOG.info("Creating initial bucket {}.", name);
                });

        return bucketStore;
    }

    @Bean
    List<String> bucketNames(File rootFolder) {
        var bucketNames = new ArrayList<String>();
        try (var paths = Files.newDirectoryStream(rootFolder.toPath())) {
            paths.forEach(
                    path -> {
                        var resolved = path.resolve(BUCKET_META_FILE);
                        if (resolved.toFile().exists()) {
                            bucketNames.add(path.getFileName().toString());
                        } else {
                            LOG.warn("Found bucket folder {} without {}", path, BUCKET_META_FILE);
                        }
                    }
            );
        } catch (IOException e) {
            throw new IllegalStateException("Could not load buckets from data directory "
                    + rootFolder, e);
        }
        return bucketNames;
    }

    @Bean
    MultipartStore multipartStore(StoreProperties properties, ObjectStore objectStore) {
        return new MultipartStore(properties.isRetainFilesOnExit(), objectStore);
    }

    @Bean
    KmsKeyStore kmsKeyStore(StoreProperties properties) {
        return new KmsKeyStore(properties.getValidKmsKeys());
    }

    @Bean
    File rootFolder(StoreProperties properties) {
        File root;
        var createTempDir = properties.getRoot() == null || properties.getRoot().isEmpty();

        if (createTempDir) {
            var baseTempDir = FileUtils.getTempDirectory().toPath();
            try {
                root = Files.createTempDirectory(baseTempDir, "s3mockFileStore").toFile();
            } catch (IOException e) {
                throw new IllegalStateException("Root folder could not be created. Base temp dir: "
                        + baseTempDir, e);
            }

            LOG.info("Successfully created \"{}\" as root folder. Will retain files on exit: {}",
                    root.getAbsolutePath(), properties.isRetainFilesOnExit());
        } else {
            root = new File(properties.getRoot());

            if (root.exists()) {
                LOG.info("Using existing folder \"{}\" as root folder. Will retain files on exit: {}",
                        root.getAbsolutePath(), properties.isRetainFilesOnExit());
                //TODO: need to validate folder structure here?
            } else if (!root.mkdirs()) {
                throw new IllegalStateException("Root folder could not be created. Path: "
                        + root.getAbsolutePath());
            } else {
                LOG.info("Successfully created \"{}\" as root folder. Will retain files on exit: {}",
                        root.getAbsolutePath(), properties.isRetainFilesOnExit());
            }
        }

        if (!properties.isRetainFilesOnExit()) {
            root.deleteOnExit();
        }

        return root;
    }
}
