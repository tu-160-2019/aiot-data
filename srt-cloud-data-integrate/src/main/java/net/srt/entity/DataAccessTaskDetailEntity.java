package net.srt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 数据接入-同步记录详情
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-28
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("data_access_task_detail")
public class DataAccessTaskDetailEntity {
	/**
	* 主键id
	*/
	@TableId
	private Long id;

	/**
	* 数据接入id
	*/
	private Long dataAccessId;

	/**
	* 数据接入任务id
	*/
	private Long taskId;

	/**
	* 源端库名
	*/
	private String sourceSchemaName;

	/**
	* 源端表名
	*/
	private String sourceTableName;

	/**
	* 目的端库名
	*/
	private String targetSchemaName;

	/**
	* 目的端表名
	*/
	private String targetTableName;

	/**
	* 同步记录数
	*/
	private Long syncCount;

	/**
	* 同步数据量
	*/
	private String syncBytes;

	/**
	* 是否成功 0-否 1-是
	*/
	private Integer ifSuccess;

	/**
	* 失败信息
	*/
	private String errorMsg;

	/**
	 * 成功信息
	 */
	private String successMsg;

	/**
	 * 项目id
	 */
	private Long projectId;

	private Long orgId;

	/**
	* 创建时间
	*/
	private Date createTime;

	private Long creator;

}
