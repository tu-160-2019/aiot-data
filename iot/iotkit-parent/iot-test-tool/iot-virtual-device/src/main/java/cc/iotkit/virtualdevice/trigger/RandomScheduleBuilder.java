/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.virtualdevice.trigger;

import org.quartz.ScheduleBuilder;
import org.quartz.spi.MutableTrigger;

public class RandomScheduleBuilder extends ScheduleBuilder<RandomTrigger> {

    private final String unit;

    public RandomScheduleBuilder(String unit) {
        this.unit = unit;
    }

    public MutableTrigger build() {
        return new RandomTrigger(unit);
    }

}
