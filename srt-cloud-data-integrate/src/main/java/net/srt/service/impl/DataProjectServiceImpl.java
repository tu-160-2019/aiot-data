package net.srt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.constants.SuperAdminEnum;
import net.srt.convert.DataProjectConvert;
import net.srt.dao.DataAccessDao;
import net.srt.dao.DataProjectDao;
import net.srt.entity.DataAccessEntity;
import net.srt.entity.DataProjectEntity;
import net.srt.entity.DataProjectUserRelEntity;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.config.Config;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.framework.security.user.SecurityUser;
import net.srt.framework.security.user.UserDetail;
import net.srt.query.DataProjectQuery;
import net.srt.service.DataAccessService;
import net.srt.service.DataProjectService;
import net.srt.service.DataProjectUserRelService;
import net.srt.vo.DataProjectVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据项目
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-09-27
 */
@Service
@AllArgsConstructor
public class DataProjectServiceImpl extends BaseServiceImpl<DataProjectDao, DataProjectEntity> implements DataProjectService {

	private final DataProjectUserRelService dataProjectUserRelService;
	private final TokenStoreCache tokenStoreCache;
	private final DataAccessService dataAccessService;

	@Override
	public PageResult<DataProjectVO> page(DataProjectQuery query) {
		IPage<DataProjectEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));
		return new PageResult<>(DataProjectConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	private LambdaQueryWrapper<DataProjectEntity> getWrapper(DataProjectQuery query) {
		LambdaQueryWrapper<DataProjectEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(query.getName()), DataProjectEntity::getName, query.getName());
		wrapper.like(StrUtil.isNotBlank(query.getEngName()), DataProjectEntity::getEngName, query.getEngName());
		wrapper.eq(query.getStatus() != null, DataProjectEntity::getStatus, query.getStatus());
		wrapper.like(StrUtil.isNotBlank(query.getDutyPerson()), DataProjectEntity::getDutyPerson, query.getDutyPerson());
		wrapper.apply(getDataScope(null, null, null, "id", false, false).getSqlFilter());
		wrapper.orderByDesc(DataProjectEntity::getCreateTime);
		wrapper.orderByDesc(DataProjectEntity::getId);
		return wrapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(DataProjectVO vo) {
		DataProjectEntity entity = DataProjectConvert.INSTANCE.convert(vo);
		setDbUrl(entity);
		baseMapper.insert(entity);
		initDb(entity);
		//添加用户到该租户下
		UserDetail user = SecurityUser.getUser();
		//把创建者放入创建的数仓下
		if (!SuperAdminEnum.YES.getValue().equals(user.getSuperAdmin())) {
			List<Long> userIds = new ArrayList<>();
			userIds.add(user.getId());
			addUser(entity.getId(), userIds);
		}
	}

	@Override
	public void update(DataProjectVO vo) {
		passOperator(vo.getId());
		DataProjectEntity entity = DataProjectConvert.INSTANCE.convert(vo);
		setDbUrl(entity);
		baseMapper.updateById(entity);
		LambdaQueryWrapper<DataAccessEntity> dataAccessEntityWrapper = Wrappers.lambdaQuery();
		//更新数据接入信息
		dataAccessEntityWrapper.eq(DataAccessEntity::getProjectId, vo.getId()).isNull(DataAccessEntity::getTargetDatabaseId);
		List<DataAccessEntity> dataAccessEntities = dataAccessService.list(dataAccessEntityWrapper);
		for (DataAccessEntity dataAccessEntity : dataAccessEntities) {
			//修改数据库的同时，同时修改一下相关的数据接入任务
			dataAccessService.update(dataAccessService.getById(dataAccessEntity.getId()));
		}
		initDb(entity);
	}

	@Override
	public void initDb(DataProjectEntity entity) {
		/*buildProjectDb(entity);
		updateById(entity);*/
		//更新缓存
		tokenStoreCache.saveProject(entity.getId(), BeanUtil.copyProperties(entity, DataProjectCacheBean::new));
	}

	private void setDbUrl(DataProjectEntity entity) {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(entity.getDbType());
		entity.setDbUrl(StringUtil.isBlank(entity.getDbUrl()) ? productTypeEnum.getUrl()
				.replace("{host}", entity.getDbIp())
				.replace("{port}", entity.getDbPort())
				.replace("{database}", entity.getDbName()) : entity.getDbUrl());
	}

	/*private void buildProjectDb(DataProjectEntity entity) {
		//建库，建用户，授权
		String dbProjectName = config.getDbProjectNameById(entity.getId());
		String dbProjectUsername = config.getDbProjectUsernameById(entity.getId());
		//如果有密码，复用原来的密码
		String dbProjectPassword = StringUtil.isNotBlank(entity.getDbPassword()) ? entity.getDbPassword() : StringUtil.getRandom2(16);
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(ProductTypeEnum.MYSQL);
		service.executeSql(config.getHouseUrl(), config.getHouseUsername(), config.getHousePassword(),
				String.format("CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_bin", dbProjectName));
		service.executeSql(config.getHouseUrl(), config.getHouseUsername(), config.getHousePassword(),
				String.format("DROP USER IF EXISTS '%s'", dbProjectUsername));
		service.executeSql(config.getHouseUrl(), config.getHouseUsername(), config.getHousePassword(),
				String.format("CREATE USER IF NOT EXISTS '%s'@'%%' IDENTIFIED BY '%s'", dbProjectUsername, dbProjectPassword));
		service.executeSql(config.getHouseUrl(), config.getHouseUsername(), config.getHousePassword(),
				String.format("GRANT ALL PRIVILEGES ON %s.* TO '%s'@'%%'", dbProjectName, dbProjectUsername));
		service.executeSql(config.getHouseUrl(), config.getHouseUsername(), config.getHousePassword(),
				"FLUSH PRIVILEGES");
		entity.setDbName(dbProjectName);
		entity.setDbUrl(config.getDbProjectUrlByName(dbProjectName));
		entity.setDbUsername(dbProjectUsername);
		entity.setDbPassword(dbProjectPassword);
	}*/

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		//项目前端禁用了批量删除，所以只会有一个
		Long projectId = idList.get(0);
		passOperator(projectId);
		//判断是否有用户与之关联
		LambdaQueryWrapper<DataProjectUserRelEntity> dataProjectUserRelEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
		dataProjectUserRelEntityLambdaQueryWrapper.eq(DataProjectUserRelEntity::getDataProjectId, projectId).last(" limit 1");
		if (dataProjectUserRelService.getOne(dataProjectUserRelEntityLambdaQueryWrapper) != null) {
			throw new ServerException("该项目下存在用户与之关联，不允许删除！");
		}
		removeByIds(idList);
		//同步删除
		tokenStoreCache.deleteProject(projectId);
	}

	private void passOperator(Long id) {
		DataProjectEntity projectEntity = baseMapper.selectById(id);
		UserDetail userDetail = SecurityUser.getUser();
		if (!SuperAdminEnum.YES.getValue().equals(userDetail.getSuperAdmin()) && !userDetail.getId().equals(projectEntity.getCreator())) {
			throw new ServerException("您无权修改或删除非自己创建的项目租户，请联系创建者或超管解决！");
		}
	}

	@Override
	public void addUser(Long projectId, List<Long> userIds) {
		userIds.forEach(userId -> {
			//判断是否已经添加
			if (dataProjectUserRelService.getByProjectIdAndUserId(projectId, userId) == null) {
				DataProjectUserRelEntity dataProjectUserRelEntity = new DataProjectUserRelEntity();
				dataProjectUserRelEntity.setDataProjectId(projectId);
				dataProjectUserRelEntity.setUserId(userId);
				dataProjectUserRelService.save(dataProjectUserRelEntity);
			}
		});
	}

	@Override
	public List<DataProjectVO> listProjects() {
		UserDetail user = SecurityUser.getUser();
		List<DataProjectEntity> dataProjectEntities;
		if (user.getSuperAdmin().equals(SuperAdminEnum.YES.getValue())) {
			LambdaQueryWrapper<DataProjectEntity> queryWrapper = new LambdaQueryWrapper<>();
			dataProjectEntities = baseMapper.selectList(queryWrapper.eq(DataProjectEntity::getStatus, 1));
		} else {
			dataProjectEntities = baseMapper.listProjects(user.getId());
		}
		return DataProjectConvert.INSTANCE.convertList(dataProjectEntities);
	}

	@Override
	public void testOnline(DataProjectVO vo) {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(vo.getDbType());
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(productTypeEnum);
		metaDataService.testQuerySQL(
				StringUtil.isBlank(vo.getDbUrl()) ? productTypeEnum.getUrl()
						.replace("{host}", vo.getDbIp())
						.replace("{port}", vo.getDbPort())
						.replace("{database}", vo.getDbName()) : vo.getDbUrl(),
				vo.getDbUsername(),
				vo.getDbPassword(),
				productTypeEnum.getTestSql()
		);
	}

}
