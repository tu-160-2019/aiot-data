package net.srt.api.module.data.governance.constant;

/**
 * @ClassName MetadataCollectRunStatus
 * @Author zrx
 */
public enum MetadataCollectRunStatus {

	/**
	 * 运行中
	 */
	RUNNING(2,"运行中"),
	/**
	 * 已完成
	 */
	SUCCESS(1,"成功"),
	/**
	 * 失败
	 */
	FAILED(0,"失败");



	private Integer code;
	private String name;

	MetadataCollectRunStatus(Integer code, String name) {
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
