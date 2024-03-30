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

package com.gitee.starblues.core;

import com.gitee.starblues.core.checker.ComposePluginLauncherChecker;
import com.gitee.starblues.core.checker.DefaultPluginLauncherChecker;
import com.gitee.starblues.core.checker.DependencyPluginLauncherChecker;
import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.exception.PluginDisabledException;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.scanner.ComposePathResolve;
import com.gitee.starblues.core.scanner.DevPathResolve;
import com.gitee.starblues.core.scanner.PathResolve;
import com.gitee.starblues.core.scanner.ProdPathResolve;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.DefaultPluginListenerFactory;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.utils.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 抽象的插件管理者
 *
 * @author starBlues
 * @version 3.1.2
 * @since 3.0.0
 */
public class DefaultPluginManager implements PluginManager {

    private final Logger log = LoggerFactory.getLogger(DefaultPluginManager.class);

    private final RealizeProvider provider;
    private final IntegrationConfiguration configuration;
    private final List<String> pluginRootDirs;

    private final PathResolve pathResolve;
    private final PluginBasicChecker basicChecker;

    protected final ComposePluginLauncherChecker launcherChecker;

    private final AtomicBoolean loaded = new AtomicBoolean(false);

    private final Map<String, PluginInsideInfo> startedPlugins = new ConcurrentHashMap<>();
    private final Map<String, PluginInsideInfo> resolvedPlugins = new ConcurrentHashMap<>();

    protected PluginListenerFactory pluginListenerFactory;


    private List<String> sortedPluginIds;

    public DefaultPluginManager(RealizeProvider realizeProvider, IntegrationConfiguration configuration) {
        this.provider = Assert.isNotNull(realizeProvider, "参数 realizeProvider 不能为空");
        this.configuration = Assert.isNotNull(configuration, "参数 configuration 不能为空");
        this.pluginRootDirs = resolvePath(configuration.pluginPath());
        this.pathResolve = getComposePathResolve();
        this.basicChecker = realizeProvider.getPluginBasicChecker();
        this.launcherChecker = getComposeLauncherChecker(realizeProvider);
        setSortedPluginIds(configuration.sortInitPluginIds());
    }

    protected ComposePluginLauncherChecker getComposeLauncherChecker(RealizeProvider realizeProvider) {
        ComposePluginLauncherChecker checker = new ComposePluginLauncherChecker();
        checker.add(new DefaultPluginLauncherChecker(realizeProvider, configuration));
        checker.add(new DependencyPluginLauncherChecker(this));
        return checker;
    }

    protected ComposePathResolve getComposePathResolve() {
        return new ComposePathResolve(new DevPathResolve(), new ProdPathResolve());
    }

    public void setSortedPluginIds(List<String> sortedPluginIds) {
        this.sortedPluginIds = sortedPluginIds;
    }

    @Override
    public List<String> getPluginsRoots() {
        return new ArrayList<>(pluginRootDirs);
    }

    @Override
    public String getDefaultPluginRoot() {
        if (pluginRootDirs == null) {
            return null;
        }
        return pluginRootDirs.stream().findFirst().orElseThrow(() -> {
            return new PluginException("插件根路径未配置");
        });
    }

    @Override
    public synchronized List<PluginInfo> loadPlugins() {
        if (loaded.get()) {
            throw new PluginException("不能重复调用: loadPlugins");
        }
        try {
            pluginListenerFactory = createPluginListenerFactory();
            if (ObjectUtils.isEmpty(pluginRootDirs)) {
                log.warn("插件根目录为空, 无法加载插件.");
                return Collections.emptyList();
            }
            List<Path> scanPluginPaths = provider.getPluginScanner().scan(pluginRootDirs);
            if (ObjectUtils.isEmpty(scanPluginPaths)) {
                printOfNotFoundPlugins();
                return Collections.emptyList();
            }
            Map<String, PluginInfo> pluginInfoMap = new LinkedHashMap<>(scanPluginPaths.size());
            boolean findException = false;
            for (Path path : scanPluginPaths) {
                try {
                    PluginInsideInfo pluginInfo = loadPlugin(path, false);
                    if (pluginInfo != null) {
                        pluginInfo.setFollowSystem();
                        PluginInfo pluginInfoFace = pluginInfo.toPluginInfo();
                        pluginListenerFactory.loadSuccess(pluginInfoFace);
                        pluginInfoMap.put(pluginInfo.getPluginId(), pluginInfoFace);
                    }
                } catch (Throwable e) {
                    pluginListenerFactory.loadFailure(path, e);
                    log.error("加载插件包失败: {}. {}", path, e.getMessage(), e);
                    findException = true;
                }
            }
            if (!findException && pluginInfoMap.isEmpty()) {
                printOfNotFoundPlugins();
            }
            return getSortPlugin(pluginInfoMap);
        } finally {
            loaded.set(true);
        }
    }

    protected PluginListenerFactory createPluginListenerFactory() {
        return new DefaultPluginListenerFactory();
    }

    @Override
    public boolean verify(Path pluginPath) {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()) {
            basicChecker.checkPath(pluginPath);
            PluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            return pluginDescriptor != null;
        } catch (Throwable e) {
            log.error("插件包校验失败: {}", pluginPath, e);
            return false;
        }
    }

    @Override
    public PluginInsideInfo parse(Path pluginPath) throws PluginException {
        if (pluginPath == null) {
            throw new PluginException("解析文件不能为 null");
        }
        PluginInsideInfo pluginInsideInfo = loadFromPath(pluginPath);
        if (pluginInsideInfo == null) {
            throw new PluginException("非法插件包: " + pluginPath);
        }
        pluginInsideInfo.setPluginState(PluginState.PARSED);
        return pluginInsideInfo;
    }

    @Override
    public PluginInsideInfo scanParse(Path pluginPath) throws PluginException {
        if (pluginPath == null) {
            throw new PluginException("解析文件不能为 null");
        }
        List<String> scanList = new ArrayList<>(1);
        scanList.add(pluginPath.toString());
        List<Path> scanPluginPaths = provider.getPluginScanner().scan(scanList);
        for (Path scanPluginPath : scanPluginPaths) {
            // 解析插件
            try {
                PluginInsideInfo pluginInsideInfo = loadFromPath(scanPluginPath);
                if (pluginInsideInfo != null) {
                    return pluginInsideInfo;
                }
            } catch (Exception e) {
                // 忽略
            }
        }
        return null;
    }

    @Override
    public synchronized PluginInsideInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        Path sourcePluginPath = pluginPath;
        File unpackPluginFile = null;
        try {
            if (unpackPlugin) {
                unpackPluginFile = PluginFileUtils.decompressZip(pluginPath.toString(), configuration.uploadTempPath());
                pluginPath = unpackPluginFile.toPath();
            }
            // 拷贝插件到root目录
            pluginPath = copyPluginToPluginRootDir(pluginPath);
            PluginInsideInfo pluginInsideInfo = scanParse(pluginPath);
            if (pluginInsideInfo == null) {
                pluginListenerFactory.loadFailure(sourcePluginPath, new PluginException("Not found PluginInsideInfo"));
                throw new PluginException("非法插件包: " + sourcePluginPath);
            }
            // 检查是否存在当前插件
            PluginInsideInfo plugin = getPlugin(pluginInsideInfo.getPluginId());
            if (plugin != null) {
                // 已经存在该插件
                unLoad(plugin.getPluginId());
            }
            pluginInsideInfo = loadPlugin(Paths.get(pluginInsideInfo.getPluginPath()), false);
            PluginInfo pluginInfoFace = pluginInsideInfo.toPluginInfo();
            pluginListenerFactory.loadSuccess(pluginInfoFace);
            return pluginInsideInfo;
        } catch (Throwable e) {
            PluginException pluginException = PluginException.getPluginException(e, () -> {
                throw new PluginException("插件包加载失败: " + sourcePluginPath, e);
            });
            pluginListenerFactory.loadFailure(sourcePluginPath, pluginException);
            if (unpackPluginFile != null) {
                FileUtils.deleteQuietly(unpackPluginFile);
            }
            FileUtils.deleteQuietly(pluginPath.toFile());
            throw pluginException;
        }
    }

    @Override
    public synchronized void unLoad(String pluginId) {
        Assert.isNotNull(pluginId, "参数pluginId不能为空");
        PluginInsideInfo pluginInsideInfo = resolvedPlugins.get(pluginId);
        if (!resolvedPlugins.containsKey(pluginId)) {
            throw new PluginException("没有发现插件: " + pluginId);
        }
        resolvedPlugins.remove(pluginId);
        pluginListenerFactory.unLoadSuccess(pluginInsideInfo.toPluginInfo());
        LogUtils.info(log, pluginInsideInfo.getPluginDescriptor(), "卸载成功");
    }

    @Override
    public synchronized PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        PluginInfo loadPluginInfo = load(pluginPath, unpackPlugin);
        if (loadPluginInfo == null) {
            throw new PluginException("插件包安装失败: " + pluginPath);
        }
        PluginInsideInfo pluginInsideInfo = resolvedPlugins.get(loadPluginInfo.getPluginId());
        PluginInfo pluginInfo = pluginInsideInfo.toPluginInfo();
        try {
            start(pluginInsideInfo);
            pluginListenerFactory.startSuccess(pluginInfo);
            log.info("插件[{}]安装成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            return pluginInsideInfo.toPluginInfo();
        } catch (Throwable e) {
            if (e instanceof PluginDisabledException) {
                throw (PluginDisabledException) e;
            }
            PluginException pluginException = PluginException.getPluginException(e, () -> {
                unLoad(loadPluginInfo.getPluginId());
                // fix https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I5GJO9
                return new PluginException("插件包[ " + pluginPath + " ]安装: " + e.getMessage(), e);
            });
            pluginListenerFactory.startFailure(pluginInfo, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized void uninstall(String pluginId) throws PluginException {
        uninstall(pluginId, PluginCloseType.UNINSTALL);
    }


    @Override
    public synchronized PluginInfo upgrade(Path pluginPath, boolean unpackPlugin) throws PluginException {
        Assert.isNotNull(pluginPath, "参数pluginPath不能为空");
        // 解析插件包
        PluginInfo upgradePlugin = parse(pluginPath);
        if (upgradePlugin == null) {
            throw new PluginException("非法插件包: " + pluginPath);
        }
        // 检查插件是否被禁用
        PluginDisabledException.checkDisabled(upgradePlugin, configuration, "更新");
        String pluginId = upgradePlugin.getPluginId();
        // 得到旧插件
        PluginInsideInfo oldPlugin = getPlugin(pluginId);
        // 检查插件版本
        PluginDescriptor upgradePluginDescriptor = upgradePlugin.getPluginDescriptor();
//        checkVersion(oldPlugin.getPluginDescriptor(), upgradePluginDescriptor);
        if (oldPlugin.getPluginState() == PluginState.STARTED) {
            // 如果插件被启动, 则卸载旧的插件
            uninstall(pluginId, PluginCloseType.UPGRADE_UNINSTALL);
        } else if (oldPlugin.getPluginState() == PluginState.LOADED) {
            // 如果插件被load
            unLoad(pluginId);
        }
        try {
            pluginPath = copyPluginToPluginRootDir(pluginPath);
            // 安装新插件
            install(pluginPath, unpackPlugin);
            log.info("更新插件[{}]成功", MsgUtils.getPluginUnique(upgradePluginDescriptor));
            return upgradePlugin;
        } catch (Throwable e) {
            throw PluginException.getPluginException(e, () ->
                    new PluginException(upgradePluginDescriptor, "更新", e));
        }
    }

    @Override
    public synchronized PluginInfo start(String pluginId) throws PluginException {
        if (ObjectUtils.isEmpty(pluginId)) {
            return null;
        }
        PluginInsideInfo pluginInsideInfo = getPlugin(pluginId);
        if (pluginInsideInfo == null) {
            throw new PluginException("没有发现插件: " + pluginId);
        }
        PluginInfo pluginInfo = pluginInsideInfo.toPluginInfo();
        try {
            start(pluginInsideInfo);
            log.info("插件[{}]启动成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            pluginListenerFactory.startSuccess(pluginInfo);
            return pluginInfo;
        } catch (Throwable e) {
            PluginException pluginException = PluginException.getPluginException(e,
                    () -> new PluginException(pluginInsideInfo.getPluginDescriptor(), "启动", e));
            pluginListenerFactory.startFailure(pluginInfo, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized PluginInfo stop(String pluginId) throws PluginException {
        if (ObjectUtils.isEmpty(pluginId)) {
            return null;
        }
        PluginInsideInfo pluginInsideInfo = getPlugin(pluginId);
        if (pluginInsideInfo == null) {
            throw new PluginException("没有发现插件: " + pluginId);
        }
        PluginInfo pluginInfo = pluginInsideInfo.toPluginInfo();
        try {
            stop(pluginInsideInfo, PluginCloseType.STOP);
            log.info("停止插件[{}]成功", MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor()));
            pluginListenerFactory.stopSuccess(pluginInfo);
            return pluginInfo;
        } catch (Throwable e) {
            PluginException pluginException = PluginException.getPluginException(e,
                    () -> new PluginException(pluginInsideInfo.getPluginDescriptor(), "停止", e));
            pluginListenerFactory.stopFailure(pluginInfo, pluginException);
            throw pluginException;
        }
    }

    @Override
    public synchronized PluginInsideInfo getPluginInfo(String pluginId) {
        if (ObjectUtils.isEmpty(pluginId)) {
            return null;
        }
        PluginInsideInfo wrapperInside = startedPlugins.get(pluginId);
        if (wrapperInside == null) {
            wrapperInside = resolvedPlugins.get(pluginId);
        }
        return wrapperInside;
    }

    @Override
    public synchronized List<PluginInsideInfo> getPluginInfos() {
        List<PluginInsideInfo> pluginDescriptors = new ArrayList<>(
                resolvedPlugins.size() + startedPlugins.size());
        pluginDescriptors.addAll(startedPlugins.values());
        pluginDescriptors.addAll(resolvedPlugins.values());
        return pluginDescriptors;
    }

    /**
     * 卸载插件
     *
     * @param pluginId  插件id
     * @param closeType 关闭类型
     * @throws PluginException 卸载异常
     */
    protected void uninstall(String pluginId, PluginCloseType closeType) throws PluginException {
        Assert.isNotNull(pluginId, "参数pluginId不能为空");
        PluginInsideInfo wrapperInside = getPlugin(pluginId);
        if (wrapperInside == null) {
            throw new PluginException("没有发现插件: " + pluginId);
        }
        PluginInfo pluginInfo = wrapperInside.toPluginInfo();
        if (wrapperInside.getPluginState() == PluginState.STARTED) {
            try {
                stop(wrapperInside, closeType);
                pluginListenerFactory.stopSuccess(pluginInfo);
            } catch (Throwable e) {
                PluginException pluginException = PluginException.getPluginException(e,
                        () -> new PluginException("停止", pluginId, e));
                pluginListenerFactory.stopFailure(pluginInfo, pluginException);
                throw pluginException;
            }
        }
        startedPlugins.remove(pluginId);
        unLoad(pluginId);
        LogUtils.info(log, wrapperInside.getPluginDescriptor(), "卸载成功");
    }

    /**
     * 加载插件信息
     *
     * @param pluginPath  插件路径
     * @param resolvePath 是否直接解析路径
     * @return 插件内部细腻些
     */
    protected PluginInsideInfo loadPlugin(Path pluginPath, boolean resolvePath) {
        if (resolvePath) {
            Path sourcePluginPath = pluginPath;
            pluginPath = pathResolve.resolve(pluginPath);
            if (pluginPath == null) {
                throw new PluginException("未发现插件: " + sourcePluginPath);
            }
        }
        PluginInsideInfo pluginInsideInfo = loadFromPath(pluginPath);
        if (pluginInsideInfo == null) {
            return null;
        }
        String pluginId = pluginInsideInfo.getPluginId();
        if (resolvedPlugins.containsKey(pluginId)) {
            throw new PluginException(pluginInsideInfo.getPluginDescriptor(), "已经被加载");
        }
        // 检查当前插件版本号是否合法
        provider.getVersionInspector().check(pluginInsideInfo.getPluginDescriptor().getPluginVersion());
        resolvedPlugins.put(pluginId, pluginInsideInfo);
        LogUtils.info(log, pluginInsideInfo.getPluginDescriptor(), "加载成功");
        return pluginInsideInfo;
    }

    protected PluginInsideInfo loadFromPath(Path pluginPath) {
        try {
            basicChecker.checkPath(pluginPath);
        } catch (Throwable e) {
            throw PluginException.getPluginException(e, () -> {
                return new PluginException("非法插件包. " + e.getMessage(), e);
            });
        }

        try (PluginDescriptorLoader pluginDescriptorLoader = provider.getPluginDescriptorLoader()) {
            InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(pluginPath);
            if (pluginDescriptor == null) {
                return null;
            }
            String pluginId = pluginDescriptor.getPluginId();
            PluginInsideInfo pluginInsideInfo = new DefaultPluginInsideInfo(pluginDescriptor);
            if (configuration.isDisabled(pluginId)) {
                pluginInsideInfo.setPluginState(PluginState.DISABLED);
            } else {
                pluginInsideInfo.setPluginState(PluginState.LOADED);
            }
            return pluginInsideInfo;
        } catch (Throwable e) {
            throw PluginException.getPluginException(e, () -> new PluginException("加载插件失败"));
        }
    }

    /**
     * 拷贝插件文件到插件根目录
     *
     * @param pluginPath 源插件文件路径
     * @return 拷贝后的插件路径
     * @throws IOException IO 异常
     */
    protected Path copyPluginToPluginRootDir(Path pluginPath) throws IOException {
        if (configuration.isDev()) {
            // 开发环境不拷贝
            return pluginPath;
        }
        File targetFile = pluginPath.toFile();
        if (!targetFile.exists()) {
            throw new PluginException("不存在插件文件: " + pluginPath);
        }
        String fileName = targetFile.getName();
        Path resultPath = null;
        // 不在插件目录
        File pluginFile = pluginPath.toFile();
        File pluginRootDir = new File(getDefaultPluginRoot());
        targetFile = Paths.get(FilesUtils.joiningFilePath(pluginRootDir.getPath(), fileName)).toFile();
        if (targetFile.exists()) {
            throw new PluginException("插件包已存在");
        }
        if (pluginFile.isFile()) {
            FileUtils.copyFile(pluginFile, targetFile);
            resultPath = targetFile.toPath();
        } else {
            FileUtils.copyDirectory(pluginFile, targetFile);
            resultPath = targetFile.toPath();
        }
        return resultPath;
    }

    /**
     * 统一启动插件操作
     *
     * @param pluginInsideInfo PluginInsideInfo
     * @throws Exception 启动异常
     */
    protected void start(PluginInsideInfo pluginInsideInfo) throws Exception {
        Assert.isNotNull(pluginInsideInfo, "pluginInsideInfo 参数不能为空");
        launcherChecker.checkCanStart(pluginInsideInfo);
        pluginInsideInfo.setPluginState(PluginState.STARTED);
        startFinish(pluginInsideInfo);
    }

    /**
     * 启动完成后的操作
     *
     * @param pluginInsideInfo pluginInsideInfo
     */
    protected void startFinish(PluginInsideInfo pluginInsideInfo) {
        String pluginId = pluginInsideInfo.getPluginId();
        startedPlugins.put(pluginId, pluginInsideInfo);
        resolvedPlugins.remove(pluginId);
    }


    /**
     * 统一停止插件操作
     *
     * @param pluginInsideInfo PluginInsideInfo
     * @param closeType        停止类型
     * @throws Exception 启动异常
     */
    protected void stop(PluginInsideInfo pluginInsideInfo, PluginCloseType closeType) throws Exception {
        launcherChecker.checkCanStop(pluginInsideInfo);
        pluginInsideInfo.setPluginState(PluginState.STOPPED);
        stopFinish(pluginInsideInfo);
    }

    /**
     * 停止完成操作
     *
     * @param pluginInsideInfo pluginInsideInfo
     */
    protected void stopFinish(PluginInsideInfo pluginInsideInfo) {
        String pluginId = pluginInsideInfo.getPluginId();
        resolvedPlugins.put(pluginId, pluginInsideInfo);
        startedPlugins.remove(pluginId);
    }

    /**
     * 根据配置重新排序插件
     *
     * @param pluginInfos 未排序的查询信息
     * @return 排序的插件信息
     */
    protected List<PluginInfo> getSortPlugin(Map<String, PluginInfo> pluginInfos) {
        if (ObjectUtils.isEmpty(pluginInfos)) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(sortedPluginIds)) {
            return new ArrayList<>(pluginInfos.values());
        }
        List<PluginInfo> sortPluginInfos = new ArrayList<>();
        for (String sortedPluginId : sortedPluginIds) {
            PluginInfo pluginInfo = pluginInfos.get(sortedPluginId);
            if (pluginInfo != null) {
                sortPluginInfos.add(pluginInfo);
                pluginInfos.remove(sortedPluginId);
            }
        }
        sortPluginInfos.addAll(pluginInfos.values());
        return sortPluginInfos;
    }


    protected PluginInsideInfo getPlugin(String pluginId) {
        PluginInsideInfo wrapperInside = startedPlugins.get(pluginId);
        if (wrapperInside == null) {
            wrapperInside = resolvedPlugins.get(pluginId);
        }
        return wrapperInside;
    }

    /**
     * 检查是否目录中是否存在同名插件
     *
     * @param dirFile        目录文件
     * @param pluginFileName 检查的插件名
     */
    private void checkExistFile(File dirFile, String pluginFileName) {
        if (ResourceUtils.existFile(dirFile, pluginFileName)) {
            // 插件根目录存在同名插件文件
            throw getExistFileException(dirFile.getPath(), pluginFileName);
        }
    }

    private PluginException getExistFileException(String rootPath, String pluginFileName) {
        return new PluginException("插件目录[" + rootPath + "]存在同名文件: " + pluginFileName);
    }

    /**
     * 检查比较插件版本
     *
     * @param oldPlugin 旧插件信息
     * @param newPlugin 新插件信息
     */
    protected void checkVersion(PluginDescriptor oldPlugin, PluginDescriptor newPlugin) {
        int compareVersion = provider.getVersionInspector().compareTo(oldPlugin.getPluginVersion(),
                newPlugin.getPluginVersion());
        if (compareVersion >= 0) {
            throw new PluginException("新插件包版本[" + MsgUtils.getPluginUnique(newPlugin) + "]必须大于" +
                    "旧插件版本[" + MsgUtils.getPluginUnique(oldPlugin) + "]");
        }
    }

    private List<String> resolvePath(List<String> path) {
        if (ObjectUtils.isEmpty(path)) {
            return Collections.emptyList();
        } else {
            File file = new File("");
            String absolutePath = file.getAbsolutePath();
            return path.stream()
                    .filter(p -> !ObjectUtils.isEmpty(p))
                    .map(p -> FilesUtils.resolveRelativePath(absolutePath, p))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 没有扫描到插件时的日志打印
     */
    private void printOfNotFoundPlugins() {
        StringBuilder warn = new StringBuilder();
        warn.append("以下路径未发现插件: \n");
        if (pluginRootDirs.size() == 1) {
            warn.append(pluginRootDirs.get(0)).append("\n");
        } else {
            for (int i = 0; i < pluginRootDirs.size(); i++) {
                warn.append(i + 1).append(". ").append(pluginRootDirs.get(i)).append("\n");
            }
        }
        warn.append("请检查路径是否合适.\n");
        warn.append("请检查配置[plugin.runMode]是否合适.\n");
        if (provider.getRuntimeMode() == RuntimeMode.DEV) {
            warn.append("请检查插件包是否编译.\n");
        } else {
            warn.append("请检查插件是否合法.\n");
        }
        log.warn(warn.toString());
    }

}
