package cc.iotkit.data.service.impl;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.ScreenApiMapper;
import cc.iotkit.data.model.TbScreenApi;
import cc.iotkit.data.service.ScreenApiService;
import cc.iotkit.model.screen.ScreenApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class ScreenApiServiceImpl extends ServiceImpl<ScreenApiMapper, TbScreenApi> implements ScreenApiService {
    @Override
    public List<ScreenApi> findByScreenId(Long id) {
        List<TbScreenApi> tbScreenApiList = this.baseMapper.selectList(new LambdaQueryWrapper<TbScreenApi>()
                .eq(TbScreenApi::getScreenId, id));
        return MapstructUtils.convert(tbScreenApiList, ScreenApi.class);
    }

    @Override
    public void deleteByScreenId(Long id) {
        this.baseMapper.delete(new LambdaQueryWrapper<TbScreenApi>()
                .eq(TbScreenApi::getScreenId, id));
    }
}
