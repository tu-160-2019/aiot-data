package net.srt.api.module.data.governance.constant;

/**
 * 这里需要跟库信息保持一致
 *
 * @ClassName MetadataProperty
 * @Author zrx
 * @Date 2023/5/22 16:15
 */
public enum BuiltInMetamodel {

	/**
	 * 数据库
	 */
	SCHEMA(2L, "Schema", "数据库", "/src/assets/database.png"),
	/**
	 * 数据表
	 */
	TABLE(3L, "Table", "数据表", "/src/assets/table.png"),
	/**
	 * 表字段
	 */
	COLUMN(4L, "Column", "表字段", "/src/assets/column.png");

	/**
	 * 注意这里的 id，metamodelInfoId，code和name和数据库metadata_property是一一对应的
	 */
	private Long id;
	private String code;
	private String name;
	private String icon;

	BuiltInMetamodel(Long id, String code, String name, String icon) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.icon = icon;
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

	public String getIcon() {
		return icon;
	}
}
