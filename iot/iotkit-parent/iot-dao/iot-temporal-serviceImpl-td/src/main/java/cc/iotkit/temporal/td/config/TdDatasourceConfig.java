/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.td.config;

import cc.iotkit.temporal.td.dao.TdTemplate;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TdDatasourceConfig {

    @Value("${spring.td-datasource.url}")
    private String url;

    @Value("${spring.td-datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.td-datasource.username}")
    private String username;

    @Value("${spring.td-datasource.password}")
    private String password;

    @Bean("tdJdbcTemplate")
    public TdTemplate tdJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return new TdTemplate(dataSource);
    }

}
