package srt.cloud.framework.dbswitch.common.entity;

import lombok.Data;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MapperConfig
 * @Author zrx
 * @Date 2024/1/8 17:06
 */
@Data
public class MapperConfig {
	private List<PatternMapper> regexTableMapper = new ArrayList<>();
	private List<PatternMapper> regexColumnMapper = new ArrayList<>();
	private List<String> sourcePrimaryKeys = new ArrayList<>();
	private String increaseColumnName;
	private ColumnDescription increaseColumn;
	//增量字段的开始和结束值
	private String increaseStartVal;
	private String increaseEndVal;
}
