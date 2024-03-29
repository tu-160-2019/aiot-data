/**
 * Copyright [2019-Present] [starBlues]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.loader;

import com.gitee.starblues.loader.jar.AbstractJarFile;
import com.gitee.starblues.loader.jar.JarFile;
import com.gitee.starblues.loader.jar.JarFileWrapper;
import com.gitee.starblues.loader.utils.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件资源存储者
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.2
 */
public class PluginResourceStorage {

    public final static Map<String, Storage> STORAGE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加插件资源
     * @param pluginId 插件id
     * @param pluginFileName 插件文件名称
     */
    public static void addPlugin(String pluginId, String pluginFileName, List<String> libPath) {
        if (STORAGE_MAP.containsKey(pluginId)) {
            return;
        }
        STORAGE_MAP.put(pluginId, new Storage(pluginFileName, libPath));
    }

    /**
     * 移除插件
     * @param pluginId 插件
     */
    public static void removePlugin(String pluginId) {
        Storage storage = STORAGE_MAP.get(pluginId);
        if (storage == null) {
            return;
        }
        IOUtils.closeQuietly(storage);
        STORAGE_MAP.remove(pluginId);
    }

    /**
     * 添加插件jar文件
     * @param jarFile jar插件文件
     */
    public static void addJarFile(AbstractJarFile jarFile) {
        STORAGE_MAP.forEach((k, v) -> {
            v.addJarFile(jarFile.getName(), jarFile);
        });
    }

    /**
     * 添加插件根的jar文件
     * @param file 插件文件
     * @param jarFile 插件jar文件
     */
    public static void addRootJarFile(File file, JarFileWrapper jarFile) {
        STORAGE_MAP.forEach((k, v) -> {
            v.addRootJarFile(file, jarFile);
        });
    }

    /**
     * 通过插件文件获取插件jar文件
     * @param file 插件文件
     * @return 插件jar文件
     */
    public static JarFileWrapper getRootJarFile(File file) {
        for (Storage value : STORAGE_MAP.values()) {
            JarFileWrapper jarFile = value.getRootJarFile(file);
            if (jarFile != null) {
                return jarFile;
            }
        }
        return null;
    }


    private static class Storage implements Closeable {
        private final String pluginFileName;
        private final List<String> libPath;
        private final Map<File, JarFileWrapper> rootJarFileMap = new ConcurrentHashMap<>();
        private final Map<String, List<AbstractJarFile>> jarFileMap = new ConcurrentHashMap<>();

        public Storage(String pluginFileName, List<String> libPath) {
            this.pluginFileName = pluginFileName;
            if (libPath == null) {
                this.libPath = Collections.emptyList();
            } else {
                this.libPath = libPath;
            }
        }

        public void addJarFile(String name, AbstractJarFile jarFile) {
            if (name == null || jarFile == null) {
                return;
            }
            if (isAddFile(name)) {
                List<AbstractJarFile> jarFiles = jarFileMap.computeIfAbsent(name, k -> new ArrayList<>());
                long count = jarFiles.stream().filter(j -> j.getName().equals(jarFile.getName())).count();
                if (count == 0) {
                    jarFiles.add(jarFile);
                }
            }
        }

        public void addRootJarFile(File file, JarFileWrapper jarFile) {
            String absolutePath = file.getAbsolutePath();
            if (isAddFile(absolutePath)) {
                rootJarFileMap.put(file, jarFile);
            }
        }

        public JarFileWrapper getRootJarFile(File file) {
            return rootJarFileMap.get(file);
        }

        @Override
        public void close() throws IOException {
            for (List<AbstractJarFile> value : jarFileMap.values()) {
                for (AbstractJarFile jarFile : value) {
                    if (jarFile == null) {
                        continue;
                    }
                    if (jarFile instanceof JarFileWrapper) {
                        ((JarFileWrapper) jarFile).canClosed();
                    }
                    IOUtils.closeQuietly(jarFile);
                }
            }
            for (JarFileWrapper jarFile : rootJarFileMap.values()) {
                if (jarFile == null) {
                    continue;
                }
                jarFile.canClosed();
                IOUtils.closeQuietly(jarFile);
            }
            jarFileMap.clear();
            rootJarFileMap.clear();
        }

        private boolean isAddFile(String path) {
            if (path.contains(pluginFileName)) {
                return true;
            }
            for (String libPath : libPath) {
                if (path.contains(libPath)) {
                    return true;
                }
            }
            return false;
        }

    }


}
