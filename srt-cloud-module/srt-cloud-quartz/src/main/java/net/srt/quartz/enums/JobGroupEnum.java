package net.srt.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName JobGroupEnum
 * @Author zrx
 * @Date 2023/1/19 16:52
 */
@Getter
@AllArgsConstructor
public enum JobGroupEnum {

	/**
	 * 默认
	 */
	DEFAULT("default"),
	/**
	 * 数据接入
	 */
	DATA_ACCESS("data_access"),
	/**
	 * 数据生产
	 */
	DATA_PRODUCTION("data_production"),
	/**
	 * 数据治理
	 */
	DATA_GOVERNANCE("data_governance"),
	/**
	 * 数据治理
	 */
	DATA_QUALITY("data_quality"),

	/**
	 * 主数据
	 */
	DATA_MASTER("data_master");


	private final String value;
}
