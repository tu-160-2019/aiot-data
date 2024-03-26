// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package net.srt.quartz.task.quality;

import net.srt.api.module.data.governance.constant.BuiltInQualityRule;
import net.srt.api.module.data.governance.dto.quality.QualityCheck;
import net.srt.quartz.task.quality.impl.DateFormatImpl;
import net.srt.quartz.task.quality.impl.RegxImpl;
import net.srt.quartz.task.quality.impl.UniquenessImpl;
import srt.cloud.framework.dbswitch.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据库写入器构造工厂类
 *
 * @author jrl
 */
public class QualityFactory {

	private static final Map<BuiltInQualityRule, Function<QualityCheck, AbstractQualityAdapter>> QUALITY_RULE_MAPPER
			= new HashMap<BuiltInQualityRule, Function<QualityCheck, AbstractQualityAdapter>>() {

		private static final long serialVersionUID = 3365136872693503697L;

		{
			put(BuiltInQualityRule.UNIQUENESS, UniquenessImpl::new);
			put(BuiltInQualityRule.PHONE_NUMBER, RegxImpl::new);
			put(BuiltInQualityRule.ID_CARD, RegxImpl::new);
			put(BuiltInQualityRule.MAIL, RegxImpl::new);
			put(BuiltInQualityRule.NUMBER_FORMAT, RegxImpl::new);
			put(BuiltInQualityRule.LENGTH_CHECK, RegxImpl::new);
			put(BuiltInQualityRule.NON_NULL_CHECK, RegxImpl::new);
			put(BuiltInQualityRule.DATE_FORMAT, DateFormatImpl::new);
		}
	};


	public static AbstractQualityAdapter createQualityAdapter(QualityCheck qualityCheck) {
		Integer ruleId = qualityCheck.getRuleId();
		BuiltInQualityRule rule = BuiltInQualityRule.getById(ruleId);
		if (rule == null) {
			throw new RuntimeException(
					String.format("Unsupported ruleId (%s)", ruleId));
		}
		if (!QUALITY_RULE_MAPPER.containsKey(rule)) {
			throw new RuntimeException(
					String.format("Unsupported rule type (%s)", rule));
		}

		AbstractQualityAdapter qualityAdapter = QUALITY_RULE_MAPPER.get(rule).apply(qualityCheck);
		if (qualityAdapter instanceof RegxImpl) {
			if (BuiltInQualityRule.PHONE_NUMBER.equals(rule)) {
				qualityAdapter.setRegx(StringUtil.REGEX_PHONE);
			} else if (BuiltInQualityRule.ID_CARD.equals(rule)) {
				qualityAdapter.setRegx(StringUtil.REGEX_IDCARD);
			} else if (BuiltInQualityRule.MAIL.equals(rule)) {
				qualityAdapter.setRegx(StringUtil.REGEX_EMAIL);
			} else if (BuiltInQualityRule.NON_NULL_CHECK.equals(rule)) {
				qualityAdapter.setRegx(StringUtil.NOT_BLANK);
			} else if (BuiltInQualityRule.NUMBER_FORMAT.equals(rule)) {
				qualityAdapter.setRegx(StringUtil.REGEX_NUMBER);
			} else if (BuiltInQualityRule.LENGTH_CHECK.equals(rule)) {
				qualityAdapter.setRegx(String.format(".{%s}", qualityCheck.getParam().getColumnLength()));
			}
		}
		return qualityAdapter;
	}

}
