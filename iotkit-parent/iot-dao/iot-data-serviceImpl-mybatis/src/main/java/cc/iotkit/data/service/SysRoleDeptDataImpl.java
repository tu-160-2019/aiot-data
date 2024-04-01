package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysRoleDept;
import cc.iotkit.data.system.ISysRoleDeptData;

import cc.iotkit.model.system.SysRoleDept;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


/**
 * author: 石恒
 * date: 2023-05-30 16:20
 * description:
 **/
@Primary
@Service
@RequiredArgsConstructor
public class SysRoleDeptDataImpl implements ISysRoleDeptData, IJPACommData<SysRoleDept, Long, TbSysRoleDept> {


    @Autowired
    private final SysRoleDeptService sysRoleDeptService;

    @Override
    public void deleteByRoleId(Collection<Long> roleIds) {
        sysRoleDeptService.remove(new LambdaQueryWrapper<TbSysRoleDept>().in(TbSysRoleDept::getRoleId, roleIds));
    }

    @Override
    public long insertBatch(List<SysRoleDept> list) {
        return sysRoleDeptService.saveBatch(MapstructUtils.convert(list, TbSysRoleDept.class)) ? list.size() : 0L;
    }

    @Override
    public SysRoleDeptService getBaseRepository() {
        return sysRoleDeptService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysRoleDept.class;
    }

    @Override
    public Class getTClass() {
        return SysRoleDept.class;
    }
}
