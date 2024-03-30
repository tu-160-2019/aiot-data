package cc.iotkit.data.manager;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.IOwnedData;
import cc.iotkit.model.rule.RuleInfo;

import java.util.List;

public interface IRuleInfoData extends IOwnedData<RuleInfo, String> {

    List<RuleInfo> findByUidAndType(String uid, String type);

    Paging<RuleInfo> findByUidAndType(String uid, String type, int page, int size);

    Paging<RuleInfo> findByType(String type, int page, int size);

}
