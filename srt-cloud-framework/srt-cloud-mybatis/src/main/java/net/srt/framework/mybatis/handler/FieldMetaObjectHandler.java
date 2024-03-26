package net.srt.framework.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import net.srt.framework.security.user.SecurityUser;
import net.srt.framework.security.user.UserDetail;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * mybatis-plus 自动填充字段
 *
 * @author 阿沐 babamu@126.com
 */
public class FieldMetaObjectHandler implements MetaObjectHandler {
	private final static String CREATE_TIME = "createTime";
	private final static String CREATOR = "creator";
	private final static String UPDATE_TIME = "updateTime";
	private final static String UPDATER = "updater";
	private final static String ORG_ID = "orgId";
	private final static String VERSION = "version";
	private final static String DELETED = "deleted";

	@Override
	public void insertFill(MetaObject metaObject) {
		UserDetail user = SecurityUser.getUser();
		if (user.getId() != null) {
			// 创建者
			setFieldValByName(CREATOR, user.getId(), metaObject);
			// 更新者
			setFieldValByName(UPDATER, user.getId(), metaObject);
			// 创建者所属机构
			setFieldValByName(ORG_ID, user.getOrgId(), metaObject);
		}
		// 创建时间
		setFieldValByName(CREATE_TIME, new Date(), metaObject);
		// 更新时间
		setFieldValByName(UPDATE_TIME, new Date(), metaObject);
		// 版本号
		setFieldValByName(VERSION, 0, metaObject);
		// 删除标识
		setFieldValByName(DELETED, 0, metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		Long userId = SecurityUser.getUserId();
		if (userId != null) {
			// 更新者
			setFieldValByName(UPDATER, userId, metaObject);
		}
		// 更新时间
		setFieldValByName(UPDATE_TIME, new Date(), metaObject);
	}
}
