package srt.cloud.framework.dbswitch.common.constant;

/**
 * @ClassName TaskType
 * @Author zrx
 */
public enum MapperType {

	/**
	 * 全量映射
	 */
	ALL(1,"统一映射"),
	/**
	 * 单独映射
	 */
	SINGLE(2,"单独映射");


	private Integer code;
	private String name;

	MapperType(Integer code, String name) {
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
