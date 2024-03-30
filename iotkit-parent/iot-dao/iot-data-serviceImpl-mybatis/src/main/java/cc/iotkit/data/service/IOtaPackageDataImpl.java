package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.IOtaPackageRepository;
import cc.iotkit.data.manager.IOtaPackageData;
import cc.iotkit.data.model.TbOtaPackage;
import cc.iotkit.model.ota.OtaPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
public class IOtaPackageDataImpl implements IOtaPackageData, IJPACommData<OtaPackage, Long> {

    private final IOtaPackageRepository iOtaPackageRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return iOtaPackageRepository;
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
        return iOtaPackageRepository.findAll().stream().map(e -> MapstructUtils.convert(e, OtaPackage.class)).collect(Collectors.toList());
    }

    @Override
    public Paging<OtaPackage> findAll(PageRequest<OtaPackage> pageRequest) {
        Page<TbOtaPackage> tbOtaPackages = iOtaPackageRepository.findAll(Pageable.ofSize(pageRequest.getPageSize()).withPage(pageRequest.getPageNum() - 1));
        return new Paging<>(tbOtaPackages.getTotalElements(), tbOtaPackages.getContent().stream().map(e -> MapstructUtils.convert(e, OtaPackage.class)).collect(Collectors.toList()));
    }
}
