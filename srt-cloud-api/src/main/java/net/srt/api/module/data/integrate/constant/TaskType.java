package net.srt.api.module.data.integrate.constant;

/**
 * @ClassName TaskType
 * @Author zrx
 */
public enum TaskType {

	/**
	 * 实时同步
	 */
	REAL_TIME_SYNC(1,"实时同步"),
	/**
	 * 一次性全量同步
	 */
	ONE_TIME_FULL_SYNC(2,"一次性全量同步"),
	/**
	 * 一次性全量周期性增量
	 */
	ONE_TIME_FULL_PERIODIC_INCR_SYNC(3,"一次性全量周期性增量");


	private Integer code;
	private String name;

	TaskType(Integer code, String name) {
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
