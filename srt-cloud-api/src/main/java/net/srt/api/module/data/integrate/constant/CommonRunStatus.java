package net.srt.api.module.data.integrate.constant;

/**
 * @ClassName CommonRunStatus
 * @Author zrx
 * @Date 2022/5/22 16:15
 */
public enum CommonRunStatus {

	/**
	 * 等待中
	 */
	WAITING(1,"等待中"),
	/**
	 * 运行中
	 */
	RUNNING(2,"运行中"),
	/**
	 * 正常
	 */
	SUCCESS(3,"正常结束"),

	/**
	 * 异常
	 */
	FAILED(4,"异常结束");


	private Integer code;
	private String name;

	CommonRunStatus(Integer code, String name) {
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
