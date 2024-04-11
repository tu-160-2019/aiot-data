package net.srt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.mybatis.entity.BaseEntity;

/**
 * 文件表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-16
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName("data_file")
public class DataFileEntity extends BaseEntity {

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 所属分组id
	 */
	private Integer fileCategoryId;

	/**
	 * 文件类型
	 */
	private String type;

	/**
	 * 文件url地址
	 */
	private String fileUrl;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 大小
	 */
	private Long size;


	/**
	 * 项目id
	 */
	private Long projectId;

	private Long orgId;


}
