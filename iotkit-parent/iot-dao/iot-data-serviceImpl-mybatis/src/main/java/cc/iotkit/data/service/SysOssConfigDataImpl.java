package cc.iotkit.data.service;

import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysOssConfig;
import cc.iotkit.data.system.ISysOssConfigData;
import cc.iotkit.model.system.SysOssConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

/**
 * @Author：tfd
 * @Date：2023/5/31 15:24
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysOssConfigDataImpl implements ISysOssConfigData, IJPACommData<SysOssConfig, Long> {
//public class SysOssConfigDataImpl implements ISysOssConfigData, IJPACommData<SysOssConfig, Long, TbSysOssConfig> {

    @Qualifier("DBSysOssConfigServiceImpl")
    private final SysOssConfigService sysOssConfigService;

    @Override
    public SysOssConfigService getBaseRepository() {
        return sysOssConfigService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysOssConfig.class;
    }

    @Override
    public Class getTClass() {
        return SysOssConfig.class;
    }


}
