package net.srt.api.module.data.governance.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接入方式
 */
@Getter
@AllArgsConstructor
public enum DistributeTaskType {
	/**
	 * 一次性
	 */
    ONCE(2),
	/**
	 * 周期性
	 */
    CRON(3);

    private final Integer value;
}
