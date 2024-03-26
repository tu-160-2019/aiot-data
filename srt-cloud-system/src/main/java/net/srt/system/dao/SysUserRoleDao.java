package net.srt.system.dao;

import net.srt.framework.mybatis.dao.BaseDao;
import net.srt.system.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关系
 *
 * @author 阿沐 babamu@126.com
 */
@Mapper
public interface SysUserRoleDao extends BaseDao<SysUserRoleEntity> {

	/**
	 * 角色ID列表
	 *
	 * @param userId 用户ID
	 * @return 返回角色ID列表
	 */
	List<Long> getRoleIdList(@Param("userId") Long userId);

	Long getByRoleIdAndUserId(@Param("roleId") Long roleId, @Param("userId") Long userId);
}
