package net.srt.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import net.srt.api.module.data.integrate.DataProjectApi;
import net.srt.framework.common.constant.Constant;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.framework.security.user.SecurityUser;
import net.srt.framework.security.user.UserDetail;
import net.srt.system.convert.SysUserConvert;
import net.srt.system.dao.SysUserDao;
import net.srt.system.entity.SysUserEntity;
import net.srt.system.enums.SuperAdminEnum;
import net.srt.system.query.SysRoleUserQuery;
import net.srt.system.query.SysUserQuery;
import net.srt.system.service.SysUserPostService;
import net.srt.system.service.SysUserRoleService;
import net.srt.system.service.SysUserService;
import net.srt.system.vo.SysUserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author 阿沐 babamu@126.com
 */
@Service
@AllArgsConstructor
public class SysUserServiceImpl extends BaseServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
	private final SysUserRoleService sysUserRoleService;
	private final SysUserPostService sysUserPostService;
	private final DataProjectApi dataProjectApi;

	@Override
	public PageResult<SysUserVO> page(SysUserQuery query) {
		// 查询参数
		Map<String, Object> params = getParams(query);
		// 分页查询
		IPage<SysUserEntity> page = getPage(query);
		params.put(Constant.PAGE, page);

		// 数据列表
		List<SysUserEntity> list = baseMapper.getList(params);

		return new PageResult<>(SysUserConvert.INSTANCE.convertList(list), page.getTotal());
	}

	private Map<String, Object> getParams(SysUserQuery query) {
		Map<String, Object> params = new HashMap<>();
		params.put("username", query.getUsername());
		params.put("mobile", query.getMobile());
		params.put("gender", query.getGender());
		params.put("projectId", query.getProjectId() == null ? getProjectId() : query.getProjectId());
		// 数据权限
		params.put(Constant.DATA_SCOPE, getDataScope("t3", "t1", null, "data_project_id", true, query.isFilterProject()));

		return params;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysUserVO vo) {
		SysUserEntity entity = SysUserConvert.INSTANCE.convert(vo);
		entity.setSuperAdmin(SuperAdminEnum.NO.getValue());

		// 判断用户名是否存在
		SysUserEntity user = baseMapper.getByUsername(entity.getUsername());
		if (user != null) {
			throw new ServerException("用户名已经存在或被其他项目租户占用");
		}

		// 判断手机号是否存在
		user = baseMapper.getByMobile(entity.getMobile());
		if (user != null) {
			throw new ServerException("手机号已经存在或被其他项目租户占用");
		}

		// 保存用户
		baseMapper.insert(entity);

		// 保存用户角色关系
		sysUserRoleService.saveOrUpdate(entity.getId(), vo.getRoleIdList());

		// 更新用户岗位关系
		sysUserPostService.saveOrUpdate(entity.getId(), vo.getPostIdList());

		dataProjectApi.addProjectRel(getProjectId(), entity.getId());
	}

	@Override
	public void update(SysUserVO vo) {
		passOperator(vo.getId());

		SysUserEntity entity = SysUserConvert.INSTANCE.convert(vo);

		// 判断用户名是否存在
		SysUserEntity user = baseMapper.getByUsername(entity.getUsername());
		if (user != null && !user.getId().equals(entity.getId())) {
			throw new ServerException("用户名已经存在或被其他项目租户占用");
		}

		// 判断手机号是否存在
		user = baseMapper.getByMobile(entity.getMobile());
		if (user != null && !user.getId().equals(entity.getId())) {
			throw new ServerException("手机号已经存在或被其他项目租户占用");
		}

		// 更新用户
		updateById(entity);

		// 更新用户角色关系
		sysUserRoleService.saveOrUpdate(entity.getId(), vo.getRoleIdList());

		// 更新用户岗位关系
		sysUserPostService.saveOrUpdate(entity.getId(), vo.getPostIdList());
	}

	private void passOperator(Long id) {
		SysUserEntity userEntity = baseMapper.selectById(id);
		UserDetail userDetail = SecurityUser.getUser();
		if (!SuperAdminEnum.YES.getValue().equals(userDetail.getSuperAdmin()) && !userDetail.getId().equals(userEntity.getCreator())) {
			throw new ServerException("您无权操作非自己创建的用户，请联系创建者或超管解决！");
		}
	}

	@Override
	public void delete(List<Long> idList) {
		for (Long id : idList) {
			passOperator(id);
		}
		// 删除用户
		removeByIds(idList);

		// 删除用户角色关系
		sysUserRoleService.deleteByUserIdList(idList);

		// 删除用户岗位关系
		sysUserPostService.deleteByUserIdList(idList);
	}

	@Override
	public SysUserVO getByMobile(String mobile) {
		SysUserEntity user = baseMapper.getByMobile(mobile);

		return SysUserConvert.INSTANCE.convert(user);
	}

	@Override
	public void updatePassword(Long id, String newPassword) {
		// 修改密码
		SysUserEntity user = getById(id);
		user.setPassword(newPassword);

		updateById(user);
	}

	@Override
	public PageResult<SysUserVO> roleUserPage(SysRoleUserQuery query) {
		// 查询参数
		Map<String, Object> params = getParams(query);
		params.remove(Constant.DATA_SCOPE);
		params.put("roleId", query.getRoleId());

		// 分页查询
		IPage<SysUserEntity> page = getPage(query);
		params.put(Constant.PAGE, page);

		// 数据列表
		List<SysUserEntity> list = baseMapper.getRoleUserList(params);

		return new PageResult<>(SysUserConvert.INSTANCE.convertList(list), page.getTotal());
	}

	@Override
	public PageResult<SysUserVO> pageProject(SysUserQuery query) {
		// 查询参数
		Map<String, Object> params = getParams(query);

		// 分页查询
		IPage<SysUserEntity> page = getPage(query);
		params.put(Constant.PAGE, page);

		// 数据列表
		List<SysUserEntity> list = baseMapper.getProjectList(params);

		return new PageResult<>(SysUserConvert.INSTANCE.convertList(list), page.getTotal());
	}

	@Override
	public void deleteProject(Long projectId, List<Long> idList) {
		UserDetail user = SecurityUser.getUser();
		for (Long userId : idList) {
			if (!SuperAdminEnum.YES.getValue().equals(user.getSuperAdmin()) && !user.getId().equals(baseMapper.getByProjectIdAndUserId(projectId, userId))) {
				throw new ServerException("要删除的数据中存在非自己添加的用户授权，您无权处理，请联系创建者或超管");
			}
			baseMapper.deleteProject(projectId, userId);
		}
	}

	@Override
	public List<Long> getProjectIds(UserDetail userDetail) {
		if (SuperAdminEnum.YES.getValue().equals(userDetail.getSuperAdmin())) {
			return null;
		}
		return baseMapper.getProjectIds(userDetail.getId());
	}

	@Override
	public List<SysUserVO> listAll() {
		List<SysUserVO> voList = SysUserConvert.INSTANCE.convertList(baseMapper.selectList(new LambdaQueryWrapper<>()));
		voList.forEach(sysUserVO -> {
			sysUserVO.setPassword(null);
		});
		return voList;
	}

	@Override
	public List<SysUserVO> listUsers() {
		// 查询参数
		SysUserQuery query = new SysUserQuery();
		query.setFilterProject(true);
		Map<String, Object> params = getParams(query);
		// 数据列表
		List<SysUserEntity> list = baseMapper.getList(params);
		return SysUserConvert.INSTANCE.convertList(list);
	}

}
