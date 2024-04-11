package net.srt.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接入方式
 */
@Getter
@AllArgsConstructor
public enum MiddleTreeNodeType {
	/**
	 * DB
	 */
    DB(1),
	/**
	 * LAYER
	 */
    LAYER(2),
	/**
	 * TABLE
	 */
	TABLE(3);

    private final Integer value;
}
