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
public class RabbitMqConfig {
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String virtualHost;
	private String exchange;
	private String routingKey;
}
