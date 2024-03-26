package srt.cloud.framework.dbswitch.common.constant;

/**
 * @ClassName TaskType
 * @Author zrx
 */
public enum ChangeDataSyncType {

	/**
	 * 主键排序比对
	 */
	PK_ORDER_COMPARE(1,"主键排序比对"),
	/**
	 * SQL
	 */
	INCREASE_COLUMN(2,"增量字段");


	private Integer code;
	private String name;

	ChangeDataSyncType(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
