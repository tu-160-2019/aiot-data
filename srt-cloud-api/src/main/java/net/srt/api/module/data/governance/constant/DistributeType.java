package net.srt.api.module.data.governance.constant;

/**
 * @ClassName MqType
 * @Author zrx
 * @Date 2023/10/8 11:06
 */
public enum DistributeType {

	DB(1, "数据库"),
	API(2, "接口"),
	MQ(3, "消息队列");


	private final Integer value;
	private final String longValue;

	DistributeType(Integer value, String longValue) {
		this.value = value;
		this.longValue = longValue;
	}

	public static DistributeType getById(Integer distributeType) {
		for (DistributeType type : DistributeType.values()) {
			if (type.getValue().equals(distributeType)) {
				return type;
			}
		}
		return null;
	}

	public Integer getValue() {
		return value;
	}

	public String getLongValue() {
		return longValue;
	}

}
