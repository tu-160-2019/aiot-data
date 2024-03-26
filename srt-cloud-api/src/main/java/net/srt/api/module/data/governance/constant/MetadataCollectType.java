package net.srt.api.module.data.governance.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接入方式
 */
@Getter
@AllArgsConstructor
public enum MetadataCollectType {
	/**
	 * 一次性
	 */
    ONCE(1),
	/**
	 * 周期性
	 */
    CRON(2);

    private final Integer value;
}
