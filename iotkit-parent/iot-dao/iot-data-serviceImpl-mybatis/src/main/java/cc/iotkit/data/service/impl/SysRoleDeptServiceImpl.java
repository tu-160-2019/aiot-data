package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysRoleDeptMapper;
import cc.iotkit.data.model.TbSysRoleDept;
import cc.iotkit.data.service.SysRoleDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class SysRoleDeptServiceImpl extends ServiceImpl<SysRoleDeptMapper, TbSysRoleDept> implements SysRoleDeptService {
}
