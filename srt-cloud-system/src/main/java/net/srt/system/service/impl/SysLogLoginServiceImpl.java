package net.srt.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.AddressUtils;
import net.srt.framework.common.utils.HttpContextUtils;
import net.srt.framework.common.utils.IpUtils;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.system.convert.SysLogLoginConvert;
import net.srt.system.dao.SysLogLoginDao;
import net.srt.system.entity.SysLogLoginEntity;
import net.srt.system.query.SysLogLoginQuery;
import net.srt.system.service.SysLogLoginService;
import net.srt.system.vo.SysLogLoginVO;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录日志
 *
 * @author 阿沐 babamu@126.com
 */
@Service
@AllArgsConstructor
public class SysLogLoginServiceImpl extends BaseServiceImpl<SysLogLoginDao, SysLogLoginEntity> implements SysLogLoginService {

    @Override
    public PageResult<SysLogLoginVO> page(SysLogLoginQuery query) {
        IPage<SysLogLoginEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

        return new PageResult<>(SysLogLoginConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
    }

    private LambdaQueryWrapper<SysLogLoginEntity> getWrapper(SysLogLoginQuery query) {
        LambdaQueryWrapper<SysLogLoginEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(query.getUsername()), SysLogLoginEntity::getUsername, query.getUsername());
        wrapper.like(StrUtil.isNotBlank(query.getAddress()), SysLogLoginEntity::getAddress, query.getAddress());
        wrapper.like(query.getStatus() != null, SysLogLoginEntity::getStatus, query.getStatus());
        wrapper.orderByDesc(SysLogLoginEntity::getId);

        return wrapper;
    }

    @Override
    public void save(String username, Integer status, Integer operation) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        assert request != null;
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String ip = IpUtils.getIpAddr(request);
        String address = AddressUtils.getAddressByIP(ip);

        SysLogLoginEntity entity = new SysLogLoginEntity();
        entity.setUsername(username);
        entity.setStatus(status);
        entity.setOperation(operation);
        entity.setIp(ip);
        entity.setAddress(address);
        entity.setUserAgent(userAgent);

        baseMapper.insert(entity);
    }

}
