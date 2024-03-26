package net.srt.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.mybatis.entity.BaseEntity;

/**
 * 机构管理
 *
 * @author 阿沐 babamu@126.com
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_org")
public class SysOrgEntity extends BaseEntity {
	/**
	 * 上级ID
	 */
	private Long pid;
	/**
	 * 机构名称
	 */
	private String name;
	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 租户id
	 */
	private Long projectId;
	/**
	 * 上级名称
	 */
	@TableField(exist = false)
	private String parentName;
}
