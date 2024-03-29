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

package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.*;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.listener.PluginInitializerListenerFactory;
import com.gitee.starblues.integration.operator.upload.UploadByInputStreamParam;
import com.gitee.starblues.integration.operator.upload.UploadByMultipartFileParam;
import com.gitee.starblues.integration.operator.upload.UploadParam;
import com.gitee.starblues.loader.launcher.DevelopmentModeSetting;
import com.gitee.starblues.spring.web.PluginStaticResourceConfig;
import com.gitee.starblues.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 默认的插件操作者
 *
 * @author starBlues
 * @version 3.1.2
 * @since 3.0.0
 */
public class DefaultPluginOperator implements PluginOperator {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final AtomicBoolean IS_INIT = new AtomicBoolean(false);

    private final GenericApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;

    private final PluginManager pluginManager;
    private final PluginInitializerListenerFactory pluginInitializerListenerFactory;

    public DefaultPluginOperator(GenericApplicationContext applicationContext,
                                 RealizeProvider realizeProvider,
                                 IntegrationConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.pluginManager = new PluginLauncherManager(realizeProvider, applicationContext, configuration);
        this.pluginInitializerListenerFactory = new PluginInitializerListenerFactory(applicationContext);
    }

    @Override
    public boolean inited() {
        return IS_INIT.get();
    }

    @Override
    public synchronized boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException {
        if (IS_INIT.get()) {
            throw new RuntimeException("插件已经被初始化了, 不能再初始化.");
        }
        try {
            log.info("插件加载环境: {}", configuration.environment().toString());
            log.info("插件加载模式: {}", DevelopmentModeSetting.getDevelopmentMode());
            pluginInitializerListenerFactory.addListener(pluginInitializerListener);
            List<String> pluginsRoots = pluginManager.getPluginsRoots();
            if (pluginsRoots.isEmpty()) {
                return true;
            }
            initBeforeLogPrint();
            if (!configuration.enable()) {
                log.info("插件功能已被禁用!");
                return false;
            }
            // 开始加载插件
            List<PluginInfo> pluginInfos = pluginManager.loadPlugins();
            if (ObjectUtils.isEmpty(pluginInfos)) {
                return false;
            }
            // 触发插件初始化监听器
            pluginInitializerListenerFactory.before();
            boolean isFoundException = false;
            IS_INIT.set(true);
            pluginInitializerListenerFactory.complete();
            log.info("插件初始化完成");
            return true;
        } catch (Exception e) {
            pluginInitializerListenerFactory.failure(e);
            throw e;
        }
    }

    /**
     * 初始化之前日志打印
     */
    private void initBeforeLogPrint() {
        List<String> pluginsRoots = pluginManager.getPluginsRoots();
        log.info("开始加载插件, 插件根路径为: \n{}", String.join("\n", pluginsRoots));
        PluginStaticResourceConfig resourceConfig = SpringBeanUtils.getExistBean(applicationContext,
                PluginStaticResourceConfig.class);
        if (resourceConfig != null) {
            resourceConfig.logPathPrefix();
        }
    }

    @Override
    public boolean verify(Path jarPath) throws PluginException {
        return pluginManager.verify(jarPath);
    }

    @Override
    public PluginInfo parse(Path pluginPath) throws PluginException {
        return toPluginInfo(pluginManager.parse(pluginPath));
    }

    @Override
    public PluginInfo install(Path jarPath, boolean unpackPlugin) throws PluginException {
        return pluginManager.install(jarPath, unpackPlugin);
    }

    @Override
    public void uninstall(String pluginId, boolean isDelete, boolean isBackup) throws PluginException {
        uninstallBackup(pluginId, isDelete, isBackup);
    }

    @Override
    public PluginInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException {
        return toPluginInfo(pluginManager.load(pluginPath, unpackPlugin));
    }

    @Override
    public boolean unload(String pluginId) throws PluginException {
        pluginManager.unLoad(pluginId);
        return true;
    }

    @Override
    public boolean start(String pluginId) throws PluginException {
        return pluginManager.start(pluginId) != null;
    }

    @Override
    public boolean stop(String pluginId) throws PluginException {
        PluginInfo pluginInfo = pluginManager.getPluginInfo(pluginId);
        if (pluginInfo == null) {
            throw new PluginException("没有发现插件: " + pluginId);
        }
        return pluginManager.stop(pluginId) != null;
    }

    @Override
    public PluginInfo uploadPlugin(UploadParam uploadParam) throws PluginException {
        Assert.isNotNull(uploadParam, "参数 uploadParam 不能为空");
        try {
            if (uploadParam instanceof UploadByInputStreamParam) {
                UploadByInputStreamParam param = (UploadByInputStreamParam) uploadParam;
                return uploadPlugin(param.getPluginFileName(), param.getInputStream(),
                        param.isBackOldPlugin(), param.isUnpackPlugin());
            } else if (uploadParam instanceof UploadByMultipartFileParam) {
                UploadByMultipartFileParam param = (UploadByMultipartFileParam) uploadParam;
                MultipartFile file = param.getPluginMultipartFile();
                return uploadPlugin(file.getOriginalFilename(), file.getInputStream(),
                        param.isBackOldPlugin(), param.isUnpackPlugin());
            } else {
                throw new PluginException("不支持上传参数: " + uploadParam.getClass().getName());
            }
        } catch (Exception e) {
            throw PluginException.getPluginException(e, () -> new PluginException(e.getMessage(), e));
        }
    }

    @Override
    public Path backupPlugin(Path backDirPath, String sign) throws PluginException {
        if (configuration.isDev()) {
            // 开发环境下不备份
            return backDirPath;
        }
        Objects.requireNonNull(backDirPath);
        return operatePluginFile(backDirPath, sign, true, false);
    }

    @Override
    public Path backupPlugin(String pluginId, String sign) throws PluginException {
        if (configuration.isDev()) {
            // 开发环境下不备份
            return null;
        }
        PluginInfo pluginInfo = getPluginInfo(pluginId);
        return backupPlugin(Paths.get(pluginInfo.getPluginPath()), sign);
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        return pluginManager.getPluginInfos().stream()
                .map(PluginInsideInfo::toPluginInfo)
                .collect(Collectors.toList());
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        PluginInsideInfo pluginInfo = pluginManager.getPluginInfo(pluginId);
        if (pluginInfo != null) {
            return pluginInfo.toPluginInfo();
        }
        return null;
    }

    /**
     * 卸载插件
     *
     * @param pluginId 插件id
     * @param isDelete 是否删除插件
     * @param isBackup 是否备份插件
     * @return 如果备份插件, 则返回备份后的插件路径
     */
    protected Path uninstallBackup(String pluginId, boolean isDelete, boolean isBackup) {
        PluginInsideInfo pluginInsideInfo = pluginManager.getPluginInfo(pluginId);
        if (pluginInsideInfo == null) {
            throw new PluginException(pluginId, "没有发现");
        }
        pluginManager.uninstall(pluginId);
        if (configuration.isDev()) {
            log.trace("开发环境对插件文件不备份和删除");
            return null;
        }
        return operatePluginFile(pluginInsideInfo, "uninstall", isBackup, isDelete);
    }

    protected PluginInfo uploadPlugin(String pluginFileName, InputStream inputStream,
                                      boolean isBackOldPlugin, boolean isUnpackPluginFile) throws Exception {
        // 获取文件的后缀名
        if (ObjectUtils.isEmpty(pluginFileName)) {
            throw new PluginException("上传的插件文件名称不能为空");
        }
        //检查文件格式是否合法
        if (!ResourceUtils.isJar(pluginFileName) && !ResourceUtils.isZip(pluginFileName)) {
            throw new PluginException("插件文件类型错误, 请上传 jar/zip 类型文件");
        }

        String tempPathString = FilesUtils.joiningFilePath(configuration.uploadTempPath(), pluginFileName);
        Path tempFilePath = Paths.get(tempPathString);
        File tempFile = PluginFileUtils.createExistFile(tempFilePath);
        // 将上传的插件拷贝到临时目录
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        File unpackPluginFile = tempFile;
        try {
            // 解析该插件包
            if (isUnpackPluginFile) {
                unpackPluginFile = PluginFileUtils.decompressZip(tempFile.toString(), configuration.uploadTempPath());
            }
            PluginInfo uploadPluginInfo = pluginManager.scanParse(unpackPluginFile.toPath());
            if (uploadPluginInfo == null) {
                Exception exception = new Exception(pluginFileName + " 文件校验失败");
                verifyFailureDelete(tempFilePath, exception);
                throw exception;
            }
            PluginInfo oldPluginInfo = getPluginInfo(uploadPluginInfo.getPluginId());
            PluginInfo pluginInfo = null;
            if (oldPluginInfo != null) {
                // 判断版本
                Path oldPluginPath = Paths.get(oldPluginInfo.getPluginPath());
                if (isBackOldPlugin) {
                    // 先备份一次旧插件
                    backupPlugin(oldPluginPath, "upload");
                }
                // 删除旧插件包
                File oldFile = oldPluginPath.toFile();
                if (oldFile.exists()) {
                    FileUtils.delete(oldPluginPath.toFile());
                }
                // 然后进入更新模式
                pluginInfo = pluginManager.upgrade(unpackPluginFile.toPath(), false);
            } else {
                // 不存在则进入安装插件模式
                pluginInfo = pluginManager.install(unpackPluginFile.toPath(), false);
            }
            return pluginInfo;
        } catch (Exception e) {
            // 出现异常, 删除刚才上传的临时文件
            verifyFailureDelete(tempFilePath, e);
            throw e;
        } finally {
            // 删除解压文件
            FileUtils.deleteQuietly(unpackPluginFile);
        }
    }

    /**
     * 备份
     *
     * @param pluginInsideInfo 插件信息
     * @param sign             文件标志
     * @param back             是否备份插件文件
     * @param delete           是否删除插件文件
     * @return 如果为备份则返回备份后的文件，否则返回null
     */
    protected Path operatePluginFile(PluginInsideInfo pluginInsideInfo, String sign, boolean back, boolean delete) {
        InsidePluginDescriptor pluginDescriptor = pluginInsideInfo.getPluginDescriptor();
        if (configuration.isDev()) {
            // 如果是开发环境, 则不进行备份
            return null;
        }
        PluginType pluginType = pluginDescriptor.getType();
        if (ObjectUtils.equalsElement(pluginType, PluginType.ZIP, PluginType.JAR, PluginType.DIR)) {
            return operatePluginFile(Paths.get(pluginInsideInfo.getPluginPath()), sign, back, delete);
        } else if (ObjectUtils.equalsElement(pluginType, PluginType.JAR_OUTER, PluginType.ZIP_OUTER)) {
            File pluginLibFile = new File(pluginDescriptor.getPluginLibDir());
            File pluginFile = new File(pluginDescriptor.getPluginPath());
            // 判断获取插件包和插件依赖是否在同一个目录，如果在返回父目录
            File parentFile = FilesUtils.sameParent(pluginLibFile, pluginFile);
            if (FilesUtils.isChildFile(pluginManager.getPluginsRoots(), parentFile)) {
                // 如果当前插件的父目录为当前插件的子目录
                // 为插件存放目录的子目录
                return operatePluginFile(parentFile.toPath(), sign, back, delete);
            } else {
                // 只备份插件jar包
                return operatePluginFile(pluginFile.toPath(), sign, back, delete);
            }
        } else {
            return operatePluginFile(Paths.get(pluginDescriptor.getPluginPath()), sign, back, delete);
        }
    }

    /**
     * 备份路径
     *
     * @param pluginPath 插件文件
     * @param sign       操作标志
     * @param back       是否备份插件文件
     * @param delete     是否删除插件文件
     * @return 如果为备份则返回备份后的文件，否则返回null
     */
    private Path operatePluginFile(Path pluginPath, String sign, boolean back, boolean delete) {
        if (!back && !delete) {
            return null;
        }
        if (pluginPath == null) {
            log.error("{}失败, 没有发现路径", sign);
            return null;
        }
        if (!Files.exists(pluginPath)) {
            log.error("{}}失败, 路径不存在: {}", sign, pluginPath);
            return null;
        }
        File sourceFile = pluginPath.toFile();
        try {
            Path targetBackPath = null;
            if (back) {
                touchBackupPath();
                String targetPathStr = configuration.backupPath() + File.separator;
                if (!ObjectUtils.isEmpty(sign)) {
                    targetPathStr = targetPathStr + sign;
                }
                targetPathStr = targetPathStr + "_" + getNowTimeByFormat() + "_" + sourceFile.getName();
                targetBackPath = Paths.get(targetPathStr);
                File targetBackFile = targetBackPath.toFile();
                copyFile(sourceFile, targetBackFile);
                log.info("备份插件文件到: {}", targetBackFile.getAbsolutePath());
            }
            if (delete) {
                if (sourceFile.isFile()) {
                    FileUtils.delete(sourceFile);
                } else {
                    FileUtils.deleteDirectory(sourceFile);
                }
            }
            return targetBackPath;
        } catch (IOException e) {
            log.error("{}路径[{}]失败: {}", sign, pluginPath, e.getMessage(), e);
            return null;
        }
    }

    private void copyFile(File sourceFile, File targetFile) throws IOException {
        if (sourceFile.isDirectory()) {
            FileUtils.copyDirectory(sourceFile, targetFile);
        } else if (sourceFile.isFile()) {
            FileUtils.copyFile(sourceFile, targetFile);
        }
    }


    /**
     * 校验文件失败后, 删除临时文件
     *
     * @param tempPluginFile 临时文件路径
     * @param e              异常信息
     * @throws Exception Exception
     */
    protected void verifyFailureDelete(Path tempPluginFile, Exception e) throws Exception {
        try {
            Files.deleteIfExists(tempPluginFile);
        } catch (IOException e1) {
            log.error("删除临时文件失败: {}. {}", tempPluginFile, e.getMessage());
        }
    }

    /**
     * 获取现在的时间
     *
     * @return String
     */
    protected String getNowTimeByFormat() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return FORMAT.format(localDateTime);
    }


    protected void touchBackupPath() throws IOException {
        String backupPath = configuration.backupPath();
        final File file = new File(backupPath);
        if (file.exists()) {
            return;
        }
        FileUtils.forceMkdir(file);
    }

    private PluginInfo toPluginInfo(PluginInsideInfo pluginInsideInfo) {
        if (pluginInsideInfo == null) {
            return null;
        }
        return pluginInsideInfo.toPluginInfo();
    }


}
