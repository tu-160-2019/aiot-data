// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统计信息
 *
 * @author jrl
 */
@Data
@AllArgsConstructor
public class PerfStat {

	private Integer index;
	private Integer total;
	private Integer failure;
	//private Long bytes;
	private Long totalRowCount;

	@Override
	public String toString() {
		return "Data Source Index: \t" + index + "\n" +
				"Total Tables Count: \t" + total + "\n" +
				"Failure Tables count: \t" + failure + "\n" +
				"Total Row count: \t" + totalRowCount + "\n";
		//"Total Transfer Size: \t" + BytesUnitUtils.bytesSizeToHuman(bytes) + "\n";
	}
}
