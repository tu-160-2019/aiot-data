package net.srt.framework.mybatis.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.constant.Constant;
import net.srt.framework.common.exception.ErrorCode;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.query.Query;
import net.srt.framework.mybatis.constant.DataScopeEnum;
import net.srt.framework.mybatis.interceptor.DataScope;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.framework.security.user.SecurityUser;
import net.srt.framework.security.user.UserDetail;
import net.srt.framework.security.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 基础服务类，所有Service都要继承
 *
 * @author 阿沐 babamu@126.com
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private TokenStoreCache storeCache;

	/**
	 * 获取分页对象
	 *
	 * @param query 分页参数
	 */
	protected IPage<T> getPage(Query query) {
		Page<T> page = new Page<>(query.getPage(), query.getLimit());

		// 排序
		if (StringUtils.isNotBlank(query.getOrder())) {
			if (query.isAsc()) {
				return page.addOrder(OrderItem.asc(query.getOrder()));
			} else {
				return page.addOrder(OrderItem.desc(query.getOrder()));
			}
		}

		return page;
	}

	/**
	 * 原生SQL 数据权限
	 *
	 * @param projectTableAlias 表别名，多表关联时，需要填写表别名
	 * @param orgTableAlias     表别名，多表关联时，需要填写表别名
	 * @param orgIdAlias        机构ID别名，null：表示org_id
	 * @param orgIdAlias        项目idID别名，null：表示project_id
	 * @return 返回数据权限
	 */
	protected DataScope getDataScope(String projectTableAlias, String orgTableAlias, String orgIdAlias, String projectIdAlias, boolean filterOrgId, boolean filterProjectId) {
		UserDetail user = SecurityUser.getUser();
		List<Long> projectIds = user.getProjectIds();
		// 如果是超级管理员，则不进行数据过滤

		// 如果为null，则设置成空字符串
		if (orgTableAlias == null) {
			orgTableAlias = "";
		}

		if (projectTableAlias == null) {
			projectTableAlias = "";
		}

		// 获取表的别名
		if (StringUtils.isNotBlank(orgTableAlias)) {
			orgTableAlias += ".";
		}

		// 获取表的别名
		if (StringUtils.isNotBlank(projectTableAlias)) {
			projectTableAlias += ".";
		}

		StringBuilder sqlFilter = new StringBuilder();
		sqlFilter.append(" ( 1=1 ");

		// 数据权限范围
		List<Long> dataScopeList = user.getDataScopeList();

		if (!user.getSuperAdmin().equals(Constant.SUPER_ADMIN)) {
			// 机构数据过滤，如果角色分配了机构的数据权限，则过滤，仅适用于有机构id的表
			if (dataScopeList != null && dataScopeList.size() > 0 && filterOrgId) {
				sqlFilter.append(" AND ");
				if (StringUtils.isBlank(orgIdAlias)) {
					orgIdAlias = "org_id";
				}
				sqlFilter.append(orgTableAlias).append(orgIdAlias);
				sqlFilter.append(" IN( ").append(StrUtil.join(",", dataScopeList)).append(" ) ");
			}
		}

		Long projectId = getProjectId();

		if (StringUtils.isBlank(projectIdAlias)) {
			projectIdAlias = "project_id";
		}
		//查看全局项目的时候不需要过滤
		if (filterProjectId) {
			sqlFilter.append(" AND ");
			sqlFilter.append(projectTableAlias).append(projectIdAlias).append("=").append(projectId);
		}

		//始终需要过滤
		if (projectIds != null && projectIds.size() > 0 && filterProjectId) {
			if (StringUtils.isBlank(projectIdAlias)) {
				projectIdAlias = "project_id";
			}
			sqlFilter.append(" AND ");
			sqlFilter.append(projectTableAlias).append(projectIdAlias);
			sqlFilter.append(" IN( ").append(StrUtil.join(",", projectIds)).append(" ) ");
		}

		if (!user.getSuperAdmin().equals(Constant.SUPER_ADMIN)) {
			if (DataScopeEnum.SELF.getValue().equals(user.getDataScope())) {
				sqlFilter.append(" AND ");
				// 查询本人的数据
				sqlFilter.append(projectTableAlias).append("creator").append("=").append(user.getId());
			}

		}

		sqlFilter.append(")");

		return new DataScope(sqlFilter.toString());
	}

	/**
	 * 获取当前的项目id
	 *
	 * @return
	 */
	@Override
	public Long getProjectId() {
		Long projectId = storeCache.getProjectId(TokenUtils.getAccessToken(request));
		//项目id过期了，重新登录
		if (projectId == null) {
			throw new ServerException(ErrorCode.UNAUTHORIZED);
		}
		return projectId;
	}

	/**
	 * 获取当前的项目数据库信息
	 *
	 * @return
	 */
	@Override
	public DataProjectCacheBean getProject() {
		DataProjectCacheBean dataProjectCacheBean = storeCache.getProject(getProjectId());
		if (dataProjectCacheBean == null) {
			throw new ServerException("没有查询到当前的项目信息，请尝试重启服务解决！");
		}
		return dataProjectCacheBean;
	}

	/**
	 * 获取当前的项目数据库信息
	 *
	 * @return
	 */
	protected String getAccessToken() {
		return TokenUtils.getAccessToken(request);
	}

	/**
	 * 根据项目id获取
	 *
	 * @return
	 */
	protected DataProjectCacheBean getProject(Long projectId) {
		DataProjectCacheBean dataProjectCacheBean = storeCache.getProject(projectId);
		if (dataProjectCacheBean == null) {
			throw new ServerException("没有查询到当前的项目信息，请尝试重启服务解决！");
		}
		return dataProjectCacheBean;
	}

	/**
	 * MyBatis-Plus 数据权限
	 */
	protected void dataScopeWithoutOrgId(LambdaQueryWrapper<T> queryWrapper) {
		DataScope dataScope = getDataScope(null, null, null, null, false, true);
		if (dataScope != null) {
			queryWrapper.apply(dataScope.getSqlFilter());
		}
	}

	/**
	 * MyBatis-Plus 数据权限
	 */
	protected void dataScopeWithOrgId(LambdaQueryWrapper<T> queryWrapper) {
		DataScope dataScope = getDataScope(null, null, null, null, true, true);
		if (dataScope != null) {
			queryWrapper.apply(dataScope.getSqlFilter());
		}
	}

}
