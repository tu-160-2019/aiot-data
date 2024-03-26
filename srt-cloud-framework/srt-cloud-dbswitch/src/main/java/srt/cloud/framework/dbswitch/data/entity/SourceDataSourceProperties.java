// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.entity;

import lombok.Data;
import srt.cloud.framework.dbswitch.common.constant.MapperType;
import srt.cloud.framework.dbswitch.common.entity.MapperConfig;
import srt.cloud.framework.dbswitch.common.entity.PatternMapper;
import srt.cloud.framework.dbswitch.common.type.DBTableType;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
public class SourceDataSourceProperties {


	private Integer poolSize = 10;
	private ProductTypeEnum sourceProductType;
	private String url;
	private String driverClassName;
	private String username;
	private String password;
	private Long connectionTimeout = TimeUnit.SECONDS.toMillis(60);
	private Long maxLifeTime = TimeUnit.MINUTES.toMillis(60);

	private Integer sourceType = 1;
	private String sourceSql;

	private Integer fetchSize = 5000;
	private DBTableType tableType;
	private String sourceSchema = "";
	private Integer includeOrExclude;
	private String sourceIncludes = "";
	private String sourceExcludes = "";

	private String mapperType = MapperType.ALL.name();
	//批量映射（适用于所有表的映射规则一样）
	private List<String> sourcePrimaryKeys;
	private String increaseColumnName;
	private ColumnDescription increaseColumn;
	private List<PatternMapper> regexTableMapper;
	private List<PatternMapper> regexColumnMapper;
	//单个表对表映射配置（（适用于对每个表的映射的规则进行个性化定制））
	private Map<String, MapperConfig> configMap;
}
