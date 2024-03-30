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

import org.apache.commons.lang3.RandomUtils;
import org.quartz.Calendar;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.Date;

public class RandomTrigger extends SimpleTriggerImpl {

    private String unit;
    private Date nextFireTime;

    public RandomTrigger(String unit) {
        this.unit = unit;
    }

    @Override
    public void triggered(Calendar calendar) {
        super.triggered(calendar);
        nextFireTime = randomTime();
    }

    @Override
    public Date getNextFireTime() {
        if (nextFireTime == null) {
            nextFireTime = randomTime();
        }
        return nextFireTime;
    }

    private Date randomTime() {
        Date previousTime = getPreviousFireTime();
        if (previousTime == null) {
            previousTime = new Date();
        }
        long time = previousTime.getTime();
        if ("second".equals(unit)) {
            time = time + RandomUtils.nextInt(0, 60) * 1000;
        } else if ("minute".equals(unit)) {
            time = time + RandomUtils.nextInt(0, 60) * 1000 * 60;
        } else if ("hour".equals(unit)) {
            time = time + RandomUtils.nextInt(0, 60) * 1000 * 60 * 60;
        }
        return new Date(time);
    }
}
