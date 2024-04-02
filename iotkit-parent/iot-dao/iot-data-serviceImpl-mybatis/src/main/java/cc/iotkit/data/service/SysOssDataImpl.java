package cc.iotkit.data.service;

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.model.TbSysOss;
import cc.iotkit.data.system.ISysOssData;
import cc.iotkit.model.system.SysOss;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysOssDataImpl implements ISysOssData, IJPACommData<SysOss, Long> {
//public class SysOssDataImpl implements ISysOssData, IJPACommData<SysOss, Long, TbSysOss> {

    @Resource
    @Qualifier("DBSysOssServiceImpl")
    private SysOssService baseRepository;


    @Override
    public SysOssService getBaseRepository() {
        return baseRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysOss.class;
    }

    @Override
    public Class getTClass() {
        return SysOss.class;
    }


}
