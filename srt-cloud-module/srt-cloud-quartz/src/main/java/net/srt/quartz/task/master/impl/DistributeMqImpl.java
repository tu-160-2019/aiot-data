package net.srt.quartz.task.master.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.constant.MqType;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterColumnDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterModelDto;
import net.srt.api.module.data.governance.dto.distribute.DistributeMq;
import net.srt.api.module.data.governance.dto.distribute.KafkaConfig;
import net.srt.api.module.data.governance.dto.distribute.RabbitMqConfig;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.quartz.task.master.AbstractMasterAdapter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ehcache.sizeof.SizeOf;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.SingletonObject;
import srt.cloud.framework.dbswitch.data.util.BytesUnitUtils;
import srt.cloud.framework.dbswitch.dbcommon.database.DatabaseOperatorFactory;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DistributeMqImpl
 * @Author zrx
 */
@Slf4j
public class DistributeMqImpl extends AbstractMasterAdapter {

	public DistributeMqImpl(DataGovernanceMasterDistributeDto distributeDto) {
		super(distributeDto);
	}

	@Override
	public void distribute() {
		try (HikariDataSource dataSource = createDataSource()) {
			DistributeMq distributeMq = distributeDto.getDistributeJson().getDistributeMq();
			RabbitTemplate rabbitTemplate = null;
			KafkaTemplate<String, String> kafkaTemplate = null;
			if (MqType.rabbitMq.equals(distributeMq.getMqType())) {
				RabbitMqConfig rabbitMqConfig = distributeMq.getRabbitMqConfig();
				CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
				connectionFactory.setHost(rabbitMqConfig.getHost());
				connectionFactory.setPort(rabbitMqConfig.getPort());
				connectionFactory.setUsername(rabbitMqConfig.getUsername());
				connectionFactory.setPassword(rabbitMqConfig.getPassword());
				connectionFactory.setVirtualHost(rabbitMqConfig.getVirtualHost());
				rabbitTemplate = new RabbitTemplate(connectionFactory);
			} else if (MqType.kafka.equals(distributeMq.getMqType())) {
				KafkaConfig kafkaConfig = distributeMq.getKafkaConfig();
				Map<String, Object> props = new HashMap<>();
				props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
				props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
				props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
				ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);
				kafkaTemplate = new KafkaTemplate<>(producerFactory);
			}
			//获取主数据信息
			DataGovernanceMasterModelDto modelDto = dataMasterApi.getMasterModelById(distributeDto.getMasterModelId()).getData();
			List<String> columns = modelDto.getColumns().stream().map(DataGovernanceMasterColumnDto::getName).collect(Collectors.toList());
			//读取数据库中的数据
			IDatabaseOperator sourceOperator = DatabaseOperatorFactory
					.createDatabaseOperator(dataSource, ProductTypeEnum.getByIndex(project.getDbType()));
			sourceOperator.setFetchSize(distributeMq.getFetchSize());
			try (StatementResultSet srs = sourceOperator.queryTableData(project.getDbSchema(), modelDto.getTableName(), columns); ResultSet rs = srs.getResultset()) {
				int size = 0;
				long dataCount = 0L;
				long bytes = 0L;
				List<Map<String, Object>> rowList = new ArrayList<>();
				while (rs.next()) {
					size++;
					dataCount++;
					Map<String, Object> map = buildRowMap(columns, rs);
					bytes += SizeOf.newInstance().sizeOf(map);
					rowList.add(map);
					if (size % distributeMq.getFetchSize() == 0) {
						sendMsg(distributeMq, rabbitTemplate, kafkaTemplate, rowList);
					}
				}
				if (!rowList.isEmpty()) {
					sendMsg(distributeMq, rabbitTemplate, kafkaTemplate, rowList);
				}
				distributeLogDto.setEndTime(new Date());
				distributeLogDto.setRunStatus(CommonRunStatus.SUCCESS.getCode());
				distributeLogDto.setDataCount(dataCount);
				distributeLogDto.setByteCount(BytesUnitUtils.bytesSizeToHuman(bytes));
				dataMasterApi.upDistributeLog(distributeLogDto);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	private void sendMsg(DistributeMq distributeMq, RabbitTemplate rabbitTemplate, KafkaTemplate<String, String> kafkaTemplate, List<Map<String, Object>> rowList) throws JsonProcessingException {
		String rowListStr = SingletonObject.OBJECT_MAPPER.writeValueAsString(rowList);
		//发送消息
		if (rabbitTemplate != null) {
			rabbitTemplate.convertAndSend(distributeMq.getRabbitMqConfig().getExchange(), distributeMq.getRabbitMqConfig().getRoutingKey(), rowListStr);
		} else if (kafkaTemplate != null) {
			kafkaTemplate.send(distributeMq.getKafkaConfig().getTopic(), rowListStr);
		}
		rowList.clear();
	}

}
