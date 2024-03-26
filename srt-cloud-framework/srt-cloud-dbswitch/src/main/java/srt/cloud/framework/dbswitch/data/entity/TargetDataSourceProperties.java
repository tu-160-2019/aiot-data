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
import srt.cloud.framework.dbswitch.common.constant.ChangeDataSyncType;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

import java.util.concurrent.TimeUnit;

@Data
public class TargetDataSourceProperties {

	private Integer poolSize = 10;
	private ProductTypeEnum targetProductType;
	private String url;
	private String driverClassName;
	private String username;
	private String password;
	private Long connectionTimeout = TimeUnit.SECONDS.toMillis(60);
	private Long maxLifeTime = TimeUnit.MINUTES.toMillis(60);

	private String targetSchema = "";
	//sql接入时有此值
	private String targetTable;
	private Boolean targetDrop = Boolean.TRUE;
	private Boolean syncExist = Boolean.TRUE;
	private Boolean onlyCreate = Boolean.FALSE;
	/**
	 * 是否同步索引信息，只有targetDrop为TRUE时生效
	 */
	private Boolean indexCreate = Boolean.FALSE;
	/**
	 * 表明前缀
	 */
	private String tablePrefix;
	/**
	 * 是否自动转为小写
	 */
	private Boolean lowercase = Boolean.FALSE;
	private Boolean uppercase = Boolean.FALSE;
	private Boolean createTableAutoIncrement = Boolean.FALSE;
	private Boolean writerEngineInsert = Boolean.TRUE;
	private Boolean changeDataSync = Boolean.FALSE;
	private String changeDataSyncType;
}
