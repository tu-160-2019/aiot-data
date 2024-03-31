package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysPost;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysPostService extends IService<TbSysPost> {

    List<TbSysPost> findByConditions(LambdaQueryWrapper<TbSysPost> lambdaQueryWrapper);

    TbSysPost findOneByConditions(LambdaQueryWrapper<TbSysPost> lambdaQueryWrapper);

    Page<TbSysPost> findPageByConditions(LambdaQueryWrapper<TbSysPost> lambdaQueryWrapper, int page, int size);

    List<Long> selectPostListByUserId(Long userId);
}
