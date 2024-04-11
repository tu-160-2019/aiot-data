package net.srt.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import net.srt.framework.mybatis.entity.BaseEntity;

import java.util.Date;

/**
 * 数仓分层
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-08
 */
@EqualsAndHashCode(callSuper=false)
@Data
@TableName("data_layer")
public class DataLayerEntity extends BaseEntity {

	/**
	* 分层英文名称
	*/
	private String name;

	/**
	* 分层中文名称
	*/
	private String cnName;

	/**
	* 分层描述
	*/
	private String note;

	/**
	* 表名前缀
	*/
	private String tablePrefix;







}
