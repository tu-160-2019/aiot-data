package net.srt.framework.common.cache.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 数据项目
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-09-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataProjectCacheBean {

	private Long id;
	/**
	 * 项目名称
	 */
	private String name;
	private String dbIp;
	private String dbPort;
	/**
	 * 数据库
	 */
	private String dbName;

	private String dbSchema;

	/**
	 * 数据库url
	 */
	private String dbUrl;

	/**
	 * 数据库用户名
	 */
	private String dbUsername;

	/**
	 * 数据库密码
	 */
	private String dbPassword;

	private Integer dbType;

}
