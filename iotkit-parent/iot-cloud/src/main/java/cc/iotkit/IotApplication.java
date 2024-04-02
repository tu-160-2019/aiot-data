/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit;

import com.gitee.starblues.loader.DevelopmentMode;
import com.gitee.starblues.loader.launcher.SpringBootstrap;
import com.gitee.starblues.loader.launcher.SpringMainBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@SpringBootApplication
@ComponentScan(excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*s3mock.*Controller")
})
@EnableTransactionManagement
@EnableWebMvc
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
public class IotApplication implements SpringBootstrap {

	public static void main(String[] args) {

		SpringMainBootstrap.launch(IotApplication.class, args);
		log.info("server start success!");
	}

	@Override
	public void run(String[] args) {
		SpringApplication.run(IotApplication.class, args);
	}

	@Override
	public String developmentMode() {
		return DevelopmentMode.ISOLATION;
	}
}
