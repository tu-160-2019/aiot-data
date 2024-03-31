package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysUserRoleMapper;
import cc.iotkit.data.model.TbSysUserRole;
import cc.iotkit.data.service.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, TbSysUserRole> implements SysUserRoleService {

}
