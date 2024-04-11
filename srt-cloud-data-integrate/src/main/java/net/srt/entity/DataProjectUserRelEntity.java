package net.srt.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import net.srt.framework.mybatis.entity.BaseEntity;

import java.util.Date;

/**
 * 项目用户关联表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-08
 */
@EqualsAndHashCode(callSuper=false)
@Data
@TableName("data_project_user_rel")
public class DataProjectUserRelEntity extends BaseEntity {

	/**
	* 项目id
	*/
	private Long dataProjectId;

	/**
	* 用户id
	*/
	private Long userId;




}
