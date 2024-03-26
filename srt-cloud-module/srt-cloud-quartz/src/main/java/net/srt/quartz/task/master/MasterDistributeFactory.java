// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package net.srt.quartz.task.master;

import net.srt.api.module.data.governance.constant.DistributeType;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.quartz.task.master.impl.DistributeApiImpl;
import net.srt.quartz.task.master.impl.DistributeDbImpl;
import net.srt.quartz.task.master.impl.DistributeMqImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据库写入器构造工厂类
 *
 * @author jrl
 */
public class MasterDistributeFactory {

	private static final Map<DistributeType, Function<DataGovernanceMasterDistributeDto, AbstractMasterAdapter>> DISTRIBUTE_MAPPER
			= new HashMap<DistributeType, Function<DataGovernanceMasterDistributeDto, AbstractMasterAdapter>>() {

		private static final long serialVersionUID = 3365136872693503697L;

		{
			put(DistributeType.DB, DistributeDbImpl::new);
			put(DistributeType.API, DistributeApiImpl::new);
			put(DistributeType.MQ, DistributeMqImpl::new);
		}
	};


	public static AbstractMasterAdapter createMasterAdapter(DataGovernanceMasterDistributeDto distributeDto) {
		Integer distributeType = distributeDto.getDistributeType();
		DistributeType type = DistributeType.getById(distributeType);
		if (type == null) {
			throw new RuntimeException(
					String.format("Unsupported distributeType (%s)", distributeType));
		}
		if (!DISTRIBUTE_MAPPER.containsKey(type)) {
			throw new RuntimeException(
					String.format("Unsupported rule type (%s)", type));
		}

		return DISTRIBUTE_MAPPER.get(type).apply(distributeDto);
	}

}
