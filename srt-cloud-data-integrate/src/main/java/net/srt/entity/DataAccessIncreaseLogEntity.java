package net.srt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.mybatis.entity.BaseEntity;

/**
 * 数据集成-数据增量接入日志
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2024-02-26
 */
@EqualsAndHashCode(callSuper=false)
@Data
@TableName("data_access_increase_log")
public class DataAccessIncreaseLogEntity extends BaseEntity {

	/**
	* 数据接入id
	*/
	private Long dataAccessId;

	/**
	* 源数据id
	*/
	private Long sourceDatabaseId;

	/**
	* 目的端数据库id（同步方式为2有此值）
	*/
	private Long targetDatabaseId;

	private String schemaName;
	/**
	* 表名
	*/
	private String tableName;

	/**
	* 增量字段
	*/
	private String increaseColumn;

	/**
	* 开始值
	*/
	private String startVal;

	/**
	* 结束值
	*/
	private String endVal;

	/**
	* 接入方式 1-接入ods 2-自定义接入
	*/
	private Integer accessMode;


	private Integer deleted;





}
