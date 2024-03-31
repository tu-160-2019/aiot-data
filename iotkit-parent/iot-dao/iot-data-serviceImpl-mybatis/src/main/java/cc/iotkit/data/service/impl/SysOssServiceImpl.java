package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysOssMapper;
import cc.iotkit.data.model.TbSysOss;
import cc.iotkit.data.service.SysOssService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service("DBSysOssServiceImpl")
@Primary
public class SysOssServiceImpl extends ServiceImpl<SysOssMapper, TbSysOss> implements SysOssService {
}
