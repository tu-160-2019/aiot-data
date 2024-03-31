package cc.iotkit.data.service;

import cc.iotkit.data.model.TbTaskInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;


public interface TaskInfoService extends IService<TbTaskInfo> {
    List<TbTaskInfo> findByUid(String uid);

    Page<TbTaskInfo> findPageByCondition (LambdaQueryWrapper<TbTaskInfo> wrapper, int page, int size);
}
