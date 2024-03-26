package net.srt.framework.common.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TreeListVo
 * @Author zrx
 * @Date 2022/11/14 14:06
 */
@Data
public class TreeNodeVo {
	private Long id;
	private Long parentId;
	private Integer ifLeaf;
	//作业类型
	private Long taskId;
	private Integer taskType;
	private String parentPath;
	private String path;
	private Integer orderNo;
	private String label;
	private Long metamodelId;
	private String name;
	private String icon;
	private String code;
	private Integer builtin;
	private String description;
	private Long projectId;
	private Long orgId;
	private Long  creator;
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createTime;
	private List<TreeNodeVo> children;
	private boolean disabled;
	private Boolean leaf;
	/**
	 * 自定义属性
	 */
	private Object attributes;
	/**
	 * 自定义类型
	 */
	private Object type;
	private Object value;
}
