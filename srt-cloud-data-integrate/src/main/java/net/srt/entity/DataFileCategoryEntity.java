package net.srt.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.srt.framework.mybatis.entity.BaseEntity;

/**
 * 文件分组表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-12
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("data_file_category")
public class DataFileCategoryEntity extends BaseEntity {

	/**
	 * 父级id（顶级为0）
	 */
	private Long parentId;

	/**
	 * 分组名称
	 */
	private String name;

	/**
	 * 分组序号
	 */
	private Integer orderNo;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 分组路径
	 */
	private String path;

	private Integer type;

	/**
	 * 项目id
	 */
	private Long projectId;

	private Long orgId;

}
