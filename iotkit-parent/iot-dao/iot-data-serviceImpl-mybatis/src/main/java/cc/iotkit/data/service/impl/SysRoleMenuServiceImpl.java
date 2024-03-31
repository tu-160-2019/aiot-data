package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysRoleMenuMapper;
import cc.iotkit.data.model.TbSysRoleMenu;
import cc.iotkit.data.service.SysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, TbSysRoleMenu> implements SysRoleMenuService {
}
