// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务参数实体类定义
 *
 * @author jrl
 */
@Data
@Builder
@AllArgsConstructor
public class TaskIncreaseParamEntity {


	private ProductTypeEnum sourceProductType;
	@NonNull
	private DataSource sourceDataSource;
	@NonNull
	private String sourceSchemaName;
	@NonNull
	private String sourceTableName;
	private ProductTypeEnum targetProductType;
	@NonNull
	private DataSource targetDataSource;
	@NonNull
	private String targetSchemaName;
	private Integer sourceType;
	private String sourceSql;
	private List<String> sourcePrimaryKeys;
	@NonNull
	private String targetTableName;
	private List<String> fieldColumns;
	@NonNull
	@Builder.Default
	private Map<String, String> columnsMap = new HashMap<>();
	private ColumnDescription increaseColumn;
	private String startVal;
	private String endVal;
}
