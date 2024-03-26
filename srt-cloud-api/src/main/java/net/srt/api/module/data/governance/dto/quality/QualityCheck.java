package net.srt.api.module.data.governance.dto.quality;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName QualityCheck
 * @Author zrx
 * @Date 2023/6/24 12:17
 */
@Data
public class QualityCheck {
	@Schema(description = "数据库类型")
	private Integer databaseType;
	@Schema(description = "库名(服务名)")
	private String databaseName;
	private String databaseSchema;
	@Schema(description = "用户名")
	private String userName;
	@Schema(description = "密码")
	private String password;
	@Schema(description = "jdbcUrl")
	private String jdbcUrl;
	@Schema(description = "个性化参数json")
	private QualityConfigParam param;
	private String tableName;
	@Schema(description = "规则id")
	private Integer ruleId;
	@Schema(description = "表id")
	private Long tableMetadataId;
	private List<QulaityColumn> qulaityColumns = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		QualityCheck that = (QualityCheck) o;
		return Objects.equals(jdbcUrl, that.jdbcUrl) &&
				Objects.equals(tableName, that.tableName) &&
				Objects.equals(ruleId, that.ruleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jdbcUrl, tableName, ruleId);
	}
}
