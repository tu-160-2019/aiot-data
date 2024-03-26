package srt.cloud.framework.dbswitch.core.model;

import lombok.Data;
import net.srt.flink.common.result.IResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class JdbcSelectResult implements IResult {

	private static final long serialVersionUID = 1L;

	private List<JdbcSelectResult> results;
	private Boolean ifQuery;
	private String sql;
	private Long time;
	private Boolean success;
	private String errorMsg;
	private Integer count;
	private List<String> columns;
	private List<Map<String, Object>> rowData;

	@Override
	public void setStartTime(LocalDateTime startTime) {

	}

	@Override
	public String getJobId() {
		return null;
	}
}
