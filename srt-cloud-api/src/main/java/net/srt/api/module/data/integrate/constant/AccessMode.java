package net.srt.api.module.data.integrate.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接入方式
 */
@Getter
@AllArgsConstructor
public enum AccessMode {
	/**
	 * ods接入
	 */
    ODS(1),
	/**
	 * 自定义接入
	 */
    CUSTOM(2);

    private final Integer value;
}
