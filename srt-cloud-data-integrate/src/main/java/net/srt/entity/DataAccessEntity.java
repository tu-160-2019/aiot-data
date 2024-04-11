package net.srt.entity;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.srt.framework.mybatis.entity.BaseEntity;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;

import java.util.Date;

/**
 * 数据集成-数据接入
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "data_access", autoResultMap = true)
public class DataAccessEntity extends BaseEntity {

	/**
	 * 任务名称
	 */
	private String taskName;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 项目id
	 */
	private Long projectId;

	private Long orgId;

	private Integer sourceType;

	/**
	 * 源端数据库id
	 */
	private Long sourceDatabaseId;

	/**
	 * 目的端数据库id
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Long targetDatabaseId;

	/**
	 * 接入方式 1-ods接入 2-自定义接入
	 */
	private Integer accessMode;

	/**
	 * 任务类型
	 */
	private Integer taskType;

	/**
	 * cron表达式
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private String cron;

	/**
	 * 发布状态
	 */
	private Integer status;

	/**
	 * 最新运行状态
	 */
	private Integer runStatus;

	/**
	 * 数据接入基础配置json
	 */
	@TableField(typeHandler = JacksonTypeHandler.class)
	private DbswichProperties dataAccessJson;

	/**
	 * 最近开始时间
	 */
	private Date startTime;

	/**
	 * 最近结束时间
	 */
	private Date endTime;

	/**
	 * 发布时间
	 */
	private Date releaseTime;

	/**
	 * 备注
	 */
	private String note;

	/**
	 * 发布人id
	 */
	private Long releaseUserId;

	/**
	 * 下次执行时间
	 */
	private Date nextRunTime;


}
