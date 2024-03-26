package net.srt.quartz.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务
 *
 * @author 阿沐 babamu@126.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("schedule_job")
public class ScheduleJobEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/**
	 * 创建者
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long creator;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

	/**
	 * 更新者
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updater;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;

	/**
	 * 版本号
	 */
	@Version
	@TableField(fill = FieldFill.INSERT)
	private Integer version;

	/**
	 * 删除标记
	 */
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	private Integer deleted;

	private Long projectId;
	private Long typeId;
	private Integer jobType;
	/**
	 * 任务名称
	 */
	private String jobName;

	/**
	 * 任务组名
	 */
	private String jobGroup;

	/**
	 * bean名称
	 */
	private String beanName;

	/**
	 * 执行方法
	 */
	private String method;

	/**
	 * 方法参数
	 */
	private String params;

	/**
	 * cron表达式
	 */
	private String cronExpression;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 是否并发  0：禁止  1：允许
	 */
	private Integer concurrent;

	/**
	 * 备注
	 */
	private String remark;

	@TableField(exist = false)
	private boolean saveLog;

	private Boolean once;

	//1-自动调度 2-手动触发
	@TableField(exist = false)
	private Integer runType;
	@TableField(exist = false)
	private String executeNo;

}
