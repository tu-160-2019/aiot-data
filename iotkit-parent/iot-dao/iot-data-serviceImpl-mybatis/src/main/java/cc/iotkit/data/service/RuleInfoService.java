package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.model.TbRuleInfo;
import cc.iotkit.model.rule.RuleInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RuleInfoService extends IService<TbRuleInfo> {
    List<RuleInfo> findByUidAndType(String uid, String type);

    Paging<RuleInfo> findByUidAndType(String uid, String type, int page, int size);

    Page<TbRuleInfo> findAll(int page, int size);

    Paging<RuleInfo> findByType(String type, int page, int size);

    List<RuleInfo> findByUid(String uid);

    Paging<RuleInfo> findByUid(String uid, int page, int size);

    long countByUid(String uid);
}
