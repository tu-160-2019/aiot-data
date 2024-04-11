package net.srt.vo;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SchemaTableDataVo {

	private Map<String, String> columns;
	private List<Map<String, Object>> rows;
}
