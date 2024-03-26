package net.srt.api.module.data.development.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接入方式
 */
@Getter
@AllArgsConstructor
public enum ExecuteType {
	/**
	 * 手动
	 */
    HAND(1),
	/**
	 * 调度
	 */
    SCHEDULE(2);

    private final Integer value;
}
