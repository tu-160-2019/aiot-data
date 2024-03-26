package net.srt.api.module.data.governance.dto.quality;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QualityParam
 * @Author zrx
 * @Date 2023/5/28 8:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityConfigParam {
	//1-单字段唯一 2-组合字段唯一
	private Integer uniqueType;
	private Integer columnLength = 1;
	private Integer columnMetaId;
	private Integer timeLength = 1;
}
