package com.gccloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.gccloud.common.constant.CommonConst;
import com.gccloud.dataroom.core.constant.DataRoomConst;
import com.gccloud.dataset.constant.DatasetConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.gccloud.common.constant.CommonConst;
import com.gccloud.dataroom.core.constant.DataRoomConst;
import com.gccloud.dataset.constant.DatasetConstant;


@MapperScan(value = {DataRoomConst.ScanPackage.DAO, DatasetConstant.ScanPackage.DAO})
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {DataRoomConst.ScanPackage.COMPONENT, DatasetConstant.ScanPackage.COMPONENT, CommonConst.ScanPackage.COMPONENT})
@MapperScan(value = {DataRoomConst.ScanPackage.DAO, DatasetConstant.ScanPackage.DAO})
public class DataRoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataRoomApplication.class, args);
	}

	/**
	 * 分页插件
	 *
	 * @return PaginationInterceptor
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
		return interceptor;
	}

}
