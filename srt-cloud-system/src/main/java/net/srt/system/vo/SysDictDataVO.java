package net.srt.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典数据
 *
 * @author 阿沐 babamu@126.com
 */
@Data
@Schema(description = "字典数据")
public class SysDictDataVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "id")
	private Long id;

	@Schema(description = "字典类型ID", required = true)
	@NotNull(message = "字典类型ID不能为空")
	private Long dictTypeId;

	@Schema(description = "字典标签", required = true)
	@NotBlank(message = "字典标签不能为空")
	private String dictLabel;

	@Schema(description = "字典值")
	private String dictValue;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "排序", required = true)
	@Min(value = 0, message = "排序值不能小于0")
	private Integer sort;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createTime;

	@Schema(description = "更新时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date updateTime;
}
