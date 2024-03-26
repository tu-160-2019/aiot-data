package net.srt.api.module.data.governance.dto.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import srt.cloud.framework.dbswitch.common.entity.PatternMapper;

import java.util.List;

/**
 * @ClassName DistributeMq
 * @Author zrx
 * @Date 2023/10/8 10:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributeDb {
	private Long databaseId;
	private List<PatternMapper> regexTableMapper;
	private List<PatternMapper> regexColumnMapper;
	private Boolean targetDrop = Boolean.TRUE;
	private Boolean syncExist = Boolean.TRUE;
	private Boolean onlyCreate = Boolean.FALSE;
	private Boolean lowercase = Boolean.FALSE;
	private Boolean uppercase = Boolean.FALSE;
	private Boolean changeDataSync = Boolean.FALSE;
	private Integer fetchSize = 5000;
}
