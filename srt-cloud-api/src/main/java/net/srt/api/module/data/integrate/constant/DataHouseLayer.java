package net.srt.api.module.data.integrate.constant;

/**
 * @ClassName DataHouseLayer
 * @Author zrx
 * @Date 2022/5/20 14:58
 */
public enum DataHouseLayer {


	/**
	 * ODS
	 */
	ODS("数据引入层","ods_"),
	/**
	 * DWD
	 */
	DWD("明细数据层","dwd_"),
	/**
	 * DIM
	 */
	DIM("维度层","dim_"),
	/**
	 * DWS
	 */
	DWS("汇总数据层","dws_"),
	/**
	 * 应用数据层
	 */
	ADS("应用数据层","ads_"),

	/**
	 * OTHER
	 */
	OTHER("其他数据","");

	private String name;
	private String tablePrefix;

	DataHouseLayer(String name, String tablePrefix) {
		this.name = name;
		this.tablePrefix = tablePrefix;
	}

	public String getName() {
		return name;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}
}
