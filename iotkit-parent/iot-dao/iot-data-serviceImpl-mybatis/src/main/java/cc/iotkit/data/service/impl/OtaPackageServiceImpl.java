package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.OtaPackageMapper;
import cc.iotkit.data.model.TbOtaPackage;
import cc.iotkit.data.service.OtaPackageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OtaPackageServiceImpl extends ServiceImpl<OtaPackageMapper, TbOtaPackage> implements OtaPackageService {
    @Override
    public Page<TbOtaPackage> findAll(int page, int size) {
        Page<TbOtaPackage> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<>());
    }
}
