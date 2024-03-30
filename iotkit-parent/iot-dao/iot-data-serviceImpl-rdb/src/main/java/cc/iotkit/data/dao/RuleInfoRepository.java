/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbRuleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleInfoRepository extends JpaRepository<TbRuleInfo, String> {

    List<TbRuleInfo> findByUid(String uid);

    List<TbRuleInfo> findByUidAndType(String uid, String type);

    Page<TbRuleInfo> findByUidAndType(String uid, String type, Pageable pageable);

    Page<TbRuleInfo> findByUid(String uid, Pageable pageable);

    Page<TbRuleInfo> findByType(String type, Pageable pageable);

    long countByUid(String uid);

}
