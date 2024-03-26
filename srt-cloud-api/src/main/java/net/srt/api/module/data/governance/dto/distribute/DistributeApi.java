package net.srt.api.module.data.governance.dto.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName DistributeApi
 * @Author zrx
 * @Date 2023/10/8 11:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributeApi {
	private String url;
	private List<ApiParam> headers;
	private List<ApiParam> params;
	private Integer fetchSize = 10;
}
