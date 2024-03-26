package net.srt.api.module.data.governance.dto.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.srt.api.module.data.governance.constant.MqType;

/**
 * @ClassName DistributeMq
 * @Author zrx
 * @Date 2023/10/8 10:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributeMq {
	private MqType mqType;
	private RabbitMqConfig rabbitMqConfig;
	private KafkaConfig kafkaConfig;
	private Integer fetchSize = 10;

}
