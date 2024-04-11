package net.srt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.mybatis.entity.BaseEntity;


/**
 * 数据项目
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-09-27
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName(value = "data_project", autoResultMap = true)
public class DataProjectEntity extends BaseEntity {

	/**
	 * 项目名称
	 */
	private String name;

	/**
	 * 英文名称
	 */
	private String engName;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 负责人
	 */
	private String dutyPerson;

	private String dbIp;
	private String dbPort;

	/**
	 * 数据库
	 */
	private String dbName;
	private String dbSchema;

	/**
	 * 数据库url
	 */
	private String dbUrl;

	/**
	 * 数据库用户名
	 */
	private String dbUsername;

	/**
	 * 数据库密码
	 */
	private String dbPassword;

	private Integer dbType;

}
