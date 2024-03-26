package net.srt.api.module.quartz.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName QuartzJobType
 * @Author zrx
 * @Date 2023/1/19 15:24
 */
@Getter
@AllArgsConstructor
public enum QuartzJobType {
	/**
	 * 自定义
	 */
	CUSTOM(1),
	/**
	 * 数据接入
	 */
	DATA_ACCESS(2),
	/**
	 * 数据生产
	 */
	DATA_PRODUCTION(3),

	/**
	 * 数据治理
	 */
	DATA_GOVERNANCE(4),

	/**
	 * 数据质量
	 */
	DATA_QUALITY(5),

	/**
	 * 主数据
	 */
	DATA_MASTER(6);

	private final Integer value;
}
