package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

import java.util.Date;

/**
* 数仓分层查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-08
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "数仓分层查询")
public class DataLayerQuery extends Query {

	@Schema(description = "分层英文名称")
	private String name;
	@Schema(description = "分层中文名称")
	private String cnName;
}
