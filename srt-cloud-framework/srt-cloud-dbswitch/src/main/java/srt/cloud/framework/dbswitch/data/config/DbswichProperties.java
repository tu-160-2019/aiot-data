// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.config;


import lombok.Data;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性映射配置
 *
 * @author jrl
 */
@Data
public class DbswichProperties {

  private List<SourceDataSourceProperties> source = new ArrayList<>();

  private TargetDataSourceProperties target = new TargetDataSourceProperties();
}
