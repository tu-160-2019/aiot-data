package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.upload.UploadParam;

import java.nio.file.Path;
import java.util.List;

/**
 * 空操作的 PluginOperator
 *
 * @author starBlues
 * @version 3.1.0
 * @since 3.0.4
 */
public class EmptyPluginOperator implements PluginOperator{

    @Override
    public boolean inited() {
        return false;
    }

    @Override
    public boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException {
        return false;
    }

    @Override
    public boolean verify(Path pluginPath) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo parse(Path pluginPath) throws PluginException {
        return null;
    }

    @Override
    public PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException {
        return null;
    }

    @Override
    public void uninstall(String pluginId, boolean isDelete, boolean isBackup) throws PluginException {

    }

    @Override
    public PluginInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException {
        return null;
    }

    @Override
    public boolean unload(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public boolean start(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public boolean stop(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo uploadPlugin(UploadParam uploadParam) throws PluginException {
        return null;
    }

    @Override
    public Path backupPlugin(Path backDirPath, String sign) throws PluginException {
        return null;
    }

    @Override
    public Path backupPlugin(String pluginId, String sign) throws PluginException {
        return null;
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        return null;
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        return null;
    }
}
