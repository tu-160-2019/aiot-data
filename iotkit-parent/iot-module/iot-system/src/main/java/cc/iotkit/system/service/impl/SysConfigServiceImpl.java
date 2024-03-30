package cc.iotkit.system.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.CacheNames;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.redis.utils.CacheUtils;
import cc.iotkit.common.service.ConfigService;
import cc.iotkit.common.utils.*;
import cc.iotkit.data.ICommonData;
import cc.iotkit.data.manager.*;
import cc.iotkit.data.system.*;
import cc.iotkit.model.system.SysConfig;
import cc.iotkit.system.dto.bo.SysConfigBo;
import cc.iotkit.system.dto.vo.SysConfigVo;
import cc.iotkit.system.service.ISysConfigService;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysConfigServiceImpl implements ISysConfigService, ConfigService {

    @Autowired
    private ISysConfigData sysConfigData;

    @Override
    public Paging<SysConfigVo> selectPageConfigList(PageRequest<SysConfigBo> query) {
        return sysConfigData.findAllByConditions(query.to(SysConfig.class)).to(SysConfigVo.class);
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfigVo selectConfigById(Long configId) {
        return sysConfigData.findById(configId).to(SysConfigVo.class);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        SysConfig sysConfig = sysConfigData.findByConfigKey(configKey);
        if (ObjectUtil.isNotNull(sysConfig)) {
            return sysConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取注册开关
     *
     * @param tenantId 租户id
     * @return true开启，false关闭
     */
    @Override
    public boolean selectRegisterEnabled(String tenantId) {
        SysConfig query = new SysConfig();
        query.setConfigKey("sys.account.registerUser");
        SysConfig retConfig = sysConfigData.findOneByCondition(query);

        if (ObjectUtil.isNull(retConfig)) {
            return false;
        }
        return Convert.toBool(retConfig.getConfigValue());
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfigVo> selectConfigList(SysConfigBo config) {

        List<SysConfig> allByCondition = sysConfigData.findAllByCondition(MapstructUtils.convert(config, SysConfig.class));
        return MapstructUtils.convert(allByCondition, SysConfigVo.class);
    }

    /**
     * 新增参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @Override
    public String insertConfig(SysConfigBo bo) {
        SysConfig to = bo.to(SysConfig.class);
        sysConfigData.save(to);
        return String.valueOf(to.getId());
    }

    /**
     * 修改参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
//    @CachePut(cacheNames = CacheNames.SYS_CONFIG, key = "#bo.configKey")
    @Override
    public String updateConfig(SysConfigBo bo) {
        SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
        if (config.getId() == null) {
            SysConfig old = sysConfigData.findByConfigKey(config.getConfigKey());
            if (old == null) {
                throw new BizException("操作失败,key不存在");
            }
            config.setId(old.getId());
        }

        sysConfigData.save(config);
        return config.getConfigValue();
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(List<Long> configIds) {
        for (Long configId : configIds) {
            SysConfig old = sysConfigData.findById(configId);
            if (StringUtils.equals(UserConstants.YES, old.getConfigType())) {
                throw new BizException(String.format("内置参数【%1$s】不能删除 ", old.getConfigKey()));
            }
            CacheUtils.evict(CacheNames.SYS_CONFIG, old.getConfigKey());
        }
        sysConfigData.deleteByIds(configIds);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        CacheUtils.clear(CacheNames.SYS_CONFIG);
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfigBo config) {
        long configId = ObjectUtil.isNull(config.getId()) ? -1L : config.getId();
        SysConfig old = sysConfigData.findByConfigKey(config.getConfigKey());
        return !ObjectUtil.isNotNull(old) || old.getId() == configId;
    }

    /**
     * 根据参数 key 获取参数值
     *
     * @param configKey 参数 key
     * @return 参数值
     */
    @Override
    public String getConfigValue(String configKey) {
        return SpringUtils.getAopProxy(this).selectConfigByKey(configKey);
    }

    @Override
    public File backupSysData() {
        File fileDir = new File("./data/backup/" + DateUtils.dateTimeNow());
        if (!fileDir.exists()) {
            FileUtil.mkdir(fileDir);
        }
        writeData(fileDir, "category", SpringUtils.getBean(ICategoryData.class));
        writeData(fileDir, "channel", SpringUtils.getBean(IChannelData.class));
        writeData(fileDir, "channelConfig", SpringUtils.getBean(IChannelConfigData.class));
        writeData(fileDir, "channelTemplate", SpringUtils.getBean(IChannelTemplateData.class));
        writeData(fileDir, "deviceGroup", SpringUtils.getBean(IDeviceGroupData.class));
        writeData(fileDir, "deviceInfo", SpringUtils.getBean(IDeviceInfoData.class));
        writeData(fileDir, "home", SpringUtils.getBean(IHomeData.class));
        writeData(fileDir, "notifyMessage", SpringUtils.getBean(INotifyMessageData.class));
        writeData(fileDir, "product", SpringUtils.getBean(IProductData.class));
        writeData(fileDir, "productModel", SpringUtils.getBean(IProductModelData.class));
        writeData(fileDir, "ruleInfo", SpringUtils.getBean(IRuleInfoData.class));
        writeData(fileDir, "space", SpringUtils.getBean(ISpaceData.class));
        writeData(fileDir, "spaceDevice", SpringUtils.getBean(ISpaceDeviceData.class));
        writeData(fileDir, "sys_app", SpringUtils.getBean(ISysAppData.class));
        writeData(fileDir, "sys_config", SpringUtils.getBean(ISysConfigData.class));
        writeData(fileDir, "sys_dept", SpringUtils.getBean(ISysDeptData.class));
        writeData(fileDir, "sys_dict_data", SpringUtils.getBean(ISysDictData.class));
        writeData(fileDir, "sys_dict_type", SpringUtils.getBean(ISysDictTypeData.class));
        writeData(fileDir, "sys_logininfor", SpringUtils.getBean(ISysLogininforData.class));
        writeData(fileDir, "sys_menu", SpringUtils.getBean(ISysMenuData.class));
        writeData(fileDir, "sys_notice", SpringUtils.getBean(ISysNoticeData.class));
        writeData(fileDir, "sys_oper_log", SpringUtils.getBean(ISysOperLogData.class));
        writeData(fileDir, "sys_oss", SpringUtils.getBean(ISysOssData.class));
        writeData(fileDir, "sys_oss_config", SpringUtils.getBean(ISysOssConfigData.class));
        writeData(fileDir, "sys_post", SpringUtils.getBean(ISysPostData.class));
        writeData(fileDir, "sys_role", SpringUtils.getBean(ISysRoleData.class));
        writeData(fileDir, "sys_role_dept", SpringUtils.getBean(ISysRoleDeptData.class));
        writeData(fileDir, "sys_role_menu", SpringUtils.getBean(ISysRoleMenuData.class));
        writeData(fileDir, "sys_tenant", SpringUtils.getBean(ISysTenantData.class));
        writeData(fileDir, "sys_tenant_package", SpringUtils.getBean(ISysTenantPackageData.class));
        writeData(fileDir, "sys_user", SpringUtils.getBean(ISysUserData.class));
        writeData(fileDir, "sys_user_post", SpringUtils.getBean(ISysUserPostData.class));
        writeData(fileDir, "sys_user_role", SpringUtils.getBean(ISysUserRoleData.class));
        writeData(fileDir, "taskInfo", SpringUtils.getBean(ITaskInfoData.class));
        writeData(fileDir, "thingModel", SpringUtils.getBean(IThingModelData.class));
        writeData(fileDir, "userInfo", SpringUtils.getBean(IUserInfoData.class));
        writeData(fileDir, "virtualDevice", SpringUtils.getBean(IVirtualDeviceData.class));
        writeData(fileDir, "pluginInfo", SpringUtils.getBean(IPluginInfoData.class));
        String zipPath = fileDir.getAbsolutePath() + ".zip";
        ZipUtil.zip(fileDir.getAbsolutePath(), zipPath);
        return new File(zipPath);
    }

    @SneakyThrows
    private void writeData(File dir, String name, ICommonData data) {
        Path path = Paths.get(dir.getAbsolutePath(), name + ".json");
        ObjectMapper mapper=new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String formattedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data.findAll());
        FileUtil.writeString(formattedJson, path.toFile(), StandardCharsets.UTF_8);
    }
}
