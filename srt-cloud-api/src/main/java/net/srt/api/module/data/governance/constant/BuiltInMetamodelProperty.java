package net.srt.api.module.data.governance.constant;

import net.srt.api.module.data.governance.dto.DataGovernanceMetadataPropertyDto;

/**
 * 这里需要跟库信息保持一致
 *
 * @ClassName MetadataProperty
 * @Author zrx
 * @Date 2023/5/22 16:15
 */
public enum BuiltInMetamodelProperty {

	/**
	 * 库注释
	 */
	SCHEMA_COMMENT(1L, 2L, "comment", "注释"),
	/**
	 * 表注释
	 */
	TABLE_COMMENT(2L, 3L, "comment", "注释"),
	/**
	 * 表空间
	 */
	TABLE_SPACE(3L, 3L, "tableSpace", "表空间"),
	/**
	 * 字段注释
	 */
	COLUMN_COMMENT(4L, 4L, "comment", "注释"),

	/**
	 * 数据类型
	 */
	COLUMN_DATA_TYPE(5L, 4L, "dataType", "数据类型"),

	/**
	 * 数据长度
	 */
	COLUMN_DATA_LENGTH(6L, 4L, "dataLength", "数据长度"),

	/**
	 * 数据精度
	 */
	COLUMN_DATA_PRECISION(7L, 4L, "dataPrecision", "数据精度"),

	/**
	 * 小数位数
	 */
	COLUMN_DATA_SCALE(8L, 4L, "dataScale", "小数位数"),

	/**
	 * 是否主键
	 */
	COLUMN_COL_KEY(9L, 4L, "colKey", "是否主键"),

	/**
	 * 是否唯一
	 */
	COLUMN_UNI_KEY(10L, 4L, "uniKey", "是否唯一"),

	/**
	 * 是否可为空
	 */
	COLUMN_NULLABLE(11L, 4L, "nullable", "是否可为空"),
	/**
	 * 是否递增
	 */
	COLUMN_AUTO_INCREMENT(12L, 4L, "autoIncrement", "是否递增"),

	/**
	 * 默认值
	 */
	COLUMN_DATA_DEFAULT(13L, 4L, "dataDefault", "默认值"),

	/**
	 * 数据库类型
	 */
	SCHEMA_TYPE(14L, 2L, "databaseType", "数据库类型"),
	/**
	 * 数据库ip
	 */
	SCHEMA_IP(15L, 2L, "ip", "数据库ip"),
	/**
	 * 数据库端口
	 */
	SCHEMA_PORT(16L, 2L, "port", "数据库端口"),
	/**
	 * 库名（服务名）
	 */
	SCHEMA_DATABASE(17L, 2L, "databaseName", "库名（服务名）"),
	/**
	 * 用户名
	 */
	SCHEMA_USERNAME(18L, 2L, "username", "用户名"),
	/**
	 * 密码
	 */
	SCHEMA_PASSWORD(19L, 2L, "password", "密码"),
	/**
	 * 密码
	 */
	SCHEMA_JDBC_URL(20L, 2L, "jdbcUrl", "jdbc连接串");

	/**
	 * 注意这里的 id，metamodelInfoId，code和name和数据库metadata_property是一一对应的
	 */
	private Long id;
	private Long metamodelInfoId;
	private String code;
	private String name;

	BuiltInMetamodelProperty(Long id, Long metamodelInfoId, String code, String name) {
		this.id = id;
		this.metamodelInfoId = metamodelInfoId;
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public Long getMetamodelInfoId() {
		return metamodelInfoId;
	}

	public static DataGovernanceMetadataPropertyDto buildProerty(BuiltInMetamodelProperty property, Long projectId, Long metadataId, String propertyVal) {
		DataGovernanceMetadataPropertyDto propertyDto = new DataGovernanceMetadataPropertyDto();
		propertyDto.setProjectId(projectId);
		propertyDto.setMetadataId(metadataId);
		propertyDto.setMetamodelPropertyId(property.getId());
		propertyDto.setProperty(propertyVal);
		return propertyDto;
	}

}
