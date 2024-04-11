package net.srt.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.srt.framework.mybatis.entity.BaseEntity;

import java.util.Date;

/**
 * 数据接入任务记录
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-26
 */
@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
@TableName("data_access_task")
@AllArgsConstructor
@NoArgsConstructor
public class DataAccessTaskEntity extends BaseEntity {

	/**
	* 数据接入任务id
	*/
	private Integer dataAccessId;

	/**
	* 运行状态（ 1-等待中 2-运行中 3-正常结束 4-异常结束）
	*/
	private Integer runStatus;

	/**
	* 开始时间
	*/
	private Date startTime;

	/**
	* 结束时间
	*/
	private Date endTime;

	private String realTimeLog;
	/**
	* 错误信息
	*/
	private String errorInfo;

	/**
	* 更新数据量
	*/
	private Long dataCount;

	/**
	 * 成功表数量
	 */
	private Long tableSuccessCount;

	/**
	 * 失败表数量
	 */
	private Long tableFailCount;

	/**
	 * 更新大小
	 */
	private String byteCount;

	/**
	 * 项目id
	 */
	private Long projectId;
	private Long orgId;
	//1-自动调度 2-手动触发
	private Integer runType;
	private String executeNo;
	private Integer deleted;

}
