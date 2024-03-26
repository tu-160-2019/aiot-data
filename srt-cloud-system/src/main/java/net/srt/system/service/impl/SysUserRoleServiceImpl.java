package net.srt.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.framework.security.user.SecurityUser;
import net.srt.framework.security.user.UserDetail;
import net.srt.system.dao.SysUserRoleDao;
import net.srt.system.entity.SysRoleEntity;
import net.srt.system.entity.SysUserRoleEntity;
import net.srt.system.enums.SuperAdminEnum;
import net.srt.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关系
 *
 * @author 阿沐 babamu@126.com
 */
@Service
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

	@Override
	public void saveOrUpdate(Long userId, List<Long> roleIdList) {
		// 数据库角色ID列表
		List<Long> dbRoleIdList = getRoleIdList(userId);

		// 需要新增的角色ID
		Collection<Long> insertRoleIdList = CollUtil.subtract(roleIdList, dbRoleIdList);
		if (CollUtil.isNotEmpty(insertRoleIdList)) {
			List<SysUserRoleEntity> roleList = insertRoleIdList.stream().map(roleId -> {
				SysUserRoleEntity entity = new SysUserRoleEntity();
				entity.setUserId(userId);
				entity.setRoleId(roleId);
				return entity;
			}).collect(Collectors.toList());

			// 批量新增
			saveBatch(roleList);
		}

		// 需要删除的角色ID
		Collection<Long> deleteRoleIdList = CollUtil.subtract(dbRoleIdList, roleIdList);
		if (CollUtil.isNotEmpty(deleteRoleIdList)) {
			LambdaQueryWrapper<SysUserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
			remove(queryWrapper.eq(SysUserRoleEntity::getUserId, userId).in(SysUserRoleEntity::getRoleId, deleteRoleIdList));
		}
	}

	@Override
	public void saveUserList(Long roleId, List<Long> userIdList) {
		for (Long userId : userIdList) {
			SysUserRoleEntity entity = new SysUserRoleEntity();
			entity.setUserId(userId);
			entity.setRoleId(roleId);
			//判断是否已存在
			if (baseMapper.getByRoleIdAndUserId(roleId,userId) == null) {
				save(entity);
			}
		}
	}

	@Override
	public void deleteByRoleIdList(List<Long> roleIdList) {
		remove(new LambdaQueryWrapper<SysUserRoleEntity>().in(SysUserRoleEntity::getRoleId, roleIdList));
	}

	@Override
	public void deleteByUserIdList(List<Long> userIdList) {
		remove(new LambdaQueryWrapper<SysUserRoleEntity>().in(SysUserRoleEntity::getUserId, userIdList));
	}

	@Override
	public void deleteByUserIdList(Long roleId, List<Long> userIdList) {

		UserDetail user = SecurityUser.getUser();
		for (Long userId : userIdList) {
			if (!SuperAdminEnum.YES.getValue().equals(user.getSuperAdmin()) && !user.getId().equals(baseMapper.getByRoleIdAndUserId(roleId, userId))) {
				throw new ServerException("要删除的数据中存在非自己添加的用户授权，您无权处理，请联系创建者或超管");
			}
		}
		LambdaQueryWrapper<SysUserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
		remove(queryWrapper.eq(SysUserRoleEntity::getRoleId, roleId).in(SysUserRoleEntity::getUserId, userIdList));
	}

	@Override
	public List<Long> getRoleIdList(Long userId) {
		return baseMapper.getRoleIdList(userId);
	}

}
