package srt.cloud.framework.dbswitch.data.domain;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName RowDataAndPk
 * @Author zrx
 * @Date 2024/3/4 16:31
 */
@Data
public class RowDataAndPk {
	private Object[] row;
	private Map<String, Object> pkVal;
}
