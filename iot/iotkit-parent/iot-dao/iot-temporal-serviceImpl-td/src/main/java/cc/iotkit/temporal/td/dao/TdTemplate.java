/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.td.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class TdTemplate extends JdbcTemplate {

    public TdTemplate() {
    }

    public TdTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public TdTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }
}
