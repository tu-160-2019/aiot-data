package net.srt.api.module.data.governance.constant;

/**
 * @ClassName BuiltInQualityRule
 * @Author zrx
 * @Date 2023/5/29 22:21
 */
public enum BuiltInQualityRule {
	/**
	 * 唯一性校验
	 */
	UNIQUENESS(1, "唯一性校验"),
	/**
	 * 手机号格式检验
	 */
	PHONE_NUMBER(2, "手机号格式检验"),
	/**
	 * 身份证号格式检验
	 */
	ID_CARD(3, "身份证号格式检验"),
	/**
	 * 邮件格式检验
	 */
	MAIL(4, "邮件格式检验"),
	/**
	 * 是否为日期格式
	 */
	DATE_FORMAT(5, "是否为日期格式"),
	/**
	 * 是否为数字格式
	 */
	NUMBER_FORMAT(6, "是否为数字格式"),
	/**
	 * 长度检验
	 */
	LENGTH_CHECK(7, "长度检验"),
	/**
	 * 非空检验
	 */
	NON_NULL_CHECK(8, "非空检验"),
	/**
	 * 关联一致性检验
	 */
	ASSOCIATION_CONSISTENCY(9, "关联一致性检验"),
	/**
	 * 关联一致性检验
	 */
	TIMELINESS(10, "及时性");

	private Integer id;
	private String name;

	BuiltInQualityRule(Integer id, String name) {
		this.id = id;
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}


	public static BuiltInQualityRule getById(Integer id) {
		for (BuiltInQualityRule qualityRule : BuiltInQualityRule.values()) {
			if (qualityRule.getId().equals(id)) {
				return qualityRule;
			}
		}
		return null;
	}

}
