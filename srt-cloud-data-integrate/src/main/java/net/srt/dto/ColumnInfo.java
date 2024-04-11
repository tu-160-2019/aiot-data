package net.srt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.srt.framework.common.utils.DateUtils;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2023-09-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public static ColumnDescription makeColumnDescription(ColumnInfo entity) {
		ColumnDescription columnDescription = new ColumnDescription();
		columnDescription.setFieldName(entity.getName());
		columnDescription.setLabelName(entity.getName());
		columnDescription.setValType(entity.getFieldType());
		columnDescription.setLength(entity.getFieldLength());
		columnDescription.setPrecision(entity.getSacle());
		columnDescription.setNullable(entity.getNullable() == 1);
		columnDescription.setRemarks(entity.getComment());
		columnDescription.setPk(entity.getPk() == 1);
		return columnDescription;
	}

	public ColumnInfo(ColumnDescription columnDescription) {
		ColumnMetaData metaData = columnDescription.getMetaData();
		this.name = columnDescription.getFieldName();
		this.comment = columnDescription.getRemarks();
		this.fieldType = metaData.getType();
		this.fieldLength = metaData.getLength();
		this.sacle = metaData.getPrecision();
		this.nullable = metaData.isNullable() ? 1 : 0;
		this.pk = columnDescription.isPk() ? 1 : 0;
	}

	@Schema(description = "字段名称")
	private String name;

	@Schema(description = "注释")
	private String comment;

	@Schema(description = "数据类型")
	private Integer fieldType;

	@Schema(description = "长度")
	private Integer fieldLength;

	@Schema(description = "小数位数")
	private Integer sacle;

	@Schema(description = "是否可为空 0-否 1-是")
	private Integer nullable;

	@Schema(description = "主键 0-否 1-是")
	private Integer pk;

}
