package net.srt.api.module.data.assets.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接入方式
 */
@Getter
@AllArgsConstructor
public enum ResourceOpenType {
	/**
	 * 全部
	 */
    ALL(1),
	/**
	 * 角色
	 */
    ROLE(2),
	/**
	 * 用户
	 */
	USER(3);

    private final Integer value;
}
