package net.srt.api.module.data.governance.dto.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DistributeJson
 * @Author zrx
 * @Date 2023/10/8 10:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributeJson {
	private DistributeDb distributeDb;
	private DistributeMq distributeMq;
	private DistributeApi distributeApi;
}
