package net.srt.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import net.srt.framework.mybatis.entity.BaseEntity;

import java.util.Date;

/**
 * 数据集成-数据库管理
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-09
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName(value = "data_database", autoResultMap = true)
public class DataDatabaseEntity extends BaseEntity {

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 数据库类型
	 */
	private Integer databaseType;

	/**
	 * 主机ip
	 */
	private String databaseIp;

	/**
	 * 端口
	 */
	private String databasePort;

	/**
	 * 库名
	 */
	private String databaseName;

	private String databaseSchema;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 是否支持实时接入
	 */
	private Integer isRtApprove;

	/**
	 * 不支持实时接入原因
	 */
	private String noRtReason;

	/**
	 * jdbcUrl
	 */
	private String jdbcUrl;

	/**
	 * 所属项目
	 */
	private Long projectId;

	private Long orgId;


}
