package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysPost;
import cc.iotkit.data.system.ISysPostData;

import cc.iotkit.model.system.SysPost;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;


/**
 * @Author：tfd
 * @Date：2023/5/30 18:20
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysPostDataImpl implements ISysPostData, IJPACommData<SysPost, Long> {
//public class SysPostDataImpl implements ISysPostData, IJPACommData<SysPost, Long, TbSysPost> {

    @Qualifier("DBSysPostServiceImpl")
    private final SysPostService sysPostService;

    @Override
    public SysPostService getBaseRepository() {
        return sysPostService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysPost.class;
    }

    @Override
    public Class getTClass() {
        return SysPost.class;
    }

    @Override
    public Paging<SysPost> findAll(PageRequest<SysPost> pageRequest) {
        Page<TbSysPost> sysPostPage = sysPostService.findPageByConditions(buildQueryCondition(pageRequest.getData()),
                pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(sysPostPage.getTotal(), MapstructUtils.convert(sysPostPage.getRecords(), SysPost.class));
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return sysPostService.selectPostListByUserId(userId);
    }

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        List<TbSysPost> ret = sysPostService.findByConditions(buildQueryCondition(post).orderByAsc(TbSysPost::getPostSort));
        return MapstructUtils.convert(ret, SysPost.class);
    }

    @Override
    public boolean checkPostNameUnique(SysPost post) {
        final TbSysPost ret = sysPostService.findOneByConditions(new LambdaQueryWrapper<TbSysPost>()
                .eq(TbSysPost::getPostName, post.getPostName())
                .eq(Objects.nonNull(post.getId()), TbSysPost::getId, post.getId()));
        return Objects.isNull(ret);
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        final TbSysPost ret = sysPostService.findOneByConditions(new LambdaQueryWrapper<TbSysPost>()
                .eq(TbSysPost::getPostCode, post.getPostCode())
                .eq(Objects.nonNull(post.getId()), TbSysPost::getId, post.getId()));

        return Objects.isNull(ret);
    }

    private LambdaQueryWrapper<TbSysPost> buildQueryCondition(SysPost post) {
        return new LambdaQueryWrapper<TbSysPost>()
                .like(StringUtils.isNotBlank(post.getPostCode()), TbSysPost::getPostCode, post.getPostCode())
                .like(StringUtils.isNotBlank(post.getPostName()), TbSysPost::getPostName, post.getPostName())
                .eq(StringUtils.isNotBlank(post.getStatus()), TbSysPost::getStatus, post.getStatus());
    }
}
