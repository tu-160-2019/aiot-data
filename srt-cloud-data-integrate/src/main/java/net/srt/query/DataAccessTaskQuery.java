package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

import java.util.Date;

/**
* 数据接入任务记录查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-24
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "数据接入任务记录查询")
public class DataAccessTaskQuery extends Query {
	private Long dataAccessId;
	private Integer runStatus;
}
