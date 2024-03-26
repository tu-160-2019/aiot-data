package net.srt.api.module.data.governance.dto.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RabbitMqConfig
 * @Author zrx
 * @Date 2023/10/8 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaConfig {
	private String bootstrapServers;
	private String topic;
}
