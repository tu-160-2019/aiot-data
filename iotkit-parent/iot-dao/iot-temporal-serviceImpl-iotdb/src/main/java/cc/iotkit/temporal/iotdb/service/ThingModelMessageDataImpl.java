/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.iotdb.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.model.stats.TimeData;
import cc.iotkit.temporal.IThingModelMessageData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThingModelMessageDataImpl implements IThingModelMessageData {

    @Override
    public Paging<ThingModelMessage> findByTypeAndIdentifier(String deviceId, String type,
                                                             String identifier,
                                                             int page, int size) {
        return new Paging<>();
    }

    @Override
    public List<TimeData> getDeviceMessageStatsWithUid(String uid, long start, long end) {
        return new ArrayList<>();
    }

    @Override
    public void add(ThingModelMessage msg) {
    }

    @Override
    public long count() {
        return 0L;
    }
}
