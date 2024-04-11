package net.srt.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import net.srt.framework.mybatis.entity.BaseEntity;

import java.util.Date;

/**
 * 数据集成-贴源数据
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-07
 */
@EqualsAndHashCode(callSuper=false)
@Data
@TableName("data_table")
public class DataTableEntity extends BaseEntity {

	/**
	* 数据接入id
	*/
	private Long dataAccessId;

	private String layer;

	private Integer ifMaster;
	/**
	* 表名
	*/
	private String tableName;

	/**
	* 注释
	*/
	private String remarks;

	/**
	* 项目id
	*/
	private Long projectId;

	/**
	* 最近同步时间
	*/
	private Date recentlySyncTime;







}
