package net.srt.api.module.data.integrate.constant;

/**
 * @ClassName TaskType
 * @Author zrx
 */
public enum AccessRunType {

	/**
	 * 自动
	 */
	AUTO(1,"自动"),
	/**
	 * 手动
	 */
	HAND(2,"手动");


	private Integer code;
	private String name;

	AccessRunType(Integer code, String name) {
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
