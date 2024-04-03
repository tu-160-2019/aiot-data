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

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.ICommonData;
import cc.iotkit.model.space.Space;

import java.util.List;

public interface ISpaceData extends ICommonData<Space,Long> {

    List<Space> findByHomeId(Long homeId);

//    List<Space> findByUidOrderByCreateAtDesc(String uid);

//    List<Space> findByUidAndHomeIdOrderByCreateAtDesc(String uid, String homeId);

//    public List<Space> findByUid(String uid);

//    public Paging<Space> findByUid(String uid, int page, int size);
}
