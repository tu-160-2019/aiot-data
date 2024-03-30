/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.manager;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.data.ICommonData;
import cc.iotkit.model.alert.AlertConfig;


public interface IAlertConfigData extends ICommonData<AlertConfig, Long> {


  Paging<AlertConfig> selectAlertConfigPage(PageRequest<AlertConfig> request);
}
