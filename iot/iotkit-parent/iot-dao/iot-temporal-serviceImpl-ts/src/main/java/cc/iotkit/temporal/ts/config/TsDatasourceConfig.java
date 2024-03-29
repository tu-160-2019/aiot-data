/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.ts.config;

import cc.iotkit.temporal.ts.dao.TsTemplate;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TsDatasourceConfig {

    @Value("${spring.ts-datasource.url}")
    private String url;

    @Value("${spring.ts-datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.ts-datasource.username}")
    private String username;

    @Value("${spring.ts-datasource.password}")
    private String password;

    @Bean("tsJdbcTemplate")
    public TsTemplate tdJdbcTemplate() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return new TsTemplate(dataSource);
    }

}
