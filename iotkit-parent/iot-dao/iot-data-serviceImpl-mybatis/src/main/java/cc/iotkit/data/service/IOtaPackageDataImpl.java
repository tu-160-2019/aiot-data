package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.IOtaPackageData;
import cc.iotkit.data.model.TbOtaPackage;
import cc.iotkit.model.ota.OtaPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 石恒
 * @Date: 2023/5/19 21:53
 * @Description:
 */
@Primary
@Service
@RequiredArgsConstructor
public class IOtaPackageDataImpl implements IOtaPackageData, IJPACommData<OtaPackage, Long, TbOtaPackage> {

    private final OtaPackageService otaPackageService;

    @Override
    public OtaPackageService getBaseRepository() {
        return otaPackageService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbOtaPackage.class;
    }

    @Override
    public Class getTClass() {
        return OtaPackage.class;
    }

    @Override
    public List<OtaPackage> findAll() {
        return otaPackageService.list().stream().map(e -> MapstructUtils.convert(e, OtaPackage.class)).collect(Collectors.toList());
    }

    @Override
    public Paging<OtaPackage> findAll(PageRequest<OtaPackage> pageRequest) {
        Page<TbOtaPackage> all = otaPackageService.findAll(pageRequest.getPageNum(), pageRequest.getPageSize());

        return new Paging<>(all.getTotal(),
                MapstructUtils.convert(all.getRecords(), OtaPackage.class));
    }
}
