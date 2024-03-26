package net.srt.api.module.data.governance.dto.quality;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QulaityColumn
 * @Author zrx
 * @Date 2023/6/24 12:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QulaityColumn {
	@Schema(description = "字段id")
	private Integer columnMetadataId;
	@Schema(description = "字段")
	private String columnName;
}
