package srt.cloud.framework.dbswitch.common.type;

/**
 * @ClassName TaskType
 * @Author zrx
 */
public enum SourceType {

	/**
	 * 指定表
	 */
	TABLE(1,"指定表"),
	/**
	 * SQL
	 */
	SQL(2,"SQL");


	private Integer code;
	private String name;

	SourceType(Integer code, String name) {
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
