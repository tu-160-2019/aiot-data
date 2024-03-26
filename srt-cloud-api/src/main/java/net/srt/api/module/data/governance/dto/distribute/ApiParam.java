package net.srt.api.module.data.governance.dto.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ApiParam
 * @Author zrx
 * @Date 2023/10/12 10:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiParam {
	private String key;
	private String value;
}
