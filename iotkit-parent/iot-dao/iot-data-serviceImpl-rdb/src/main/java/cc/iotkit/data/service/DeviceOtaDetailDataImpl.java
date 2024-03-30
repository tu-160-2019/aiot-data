package cc.iotkit.data.service;

import cc.iotkit.data.dao.DeviceOtaDetailRepository;
import cc.iotkit.data.dao.DeviceOtaInfoRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IDeviceOtaDetailData;
import cc.iotkit.data.manager.IDeviceOtaInfoData;
import cc.iotkit.data.model.TbDeviceOtaDetail;
import cc.iotkit.data.model.TbDeviceOtaInfo;
import cc.iotkit.model.ota.DeviceOtaDetail;
import cc.iotkit.model.ota.DeviceOtaInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @Author: 石恒
 * @Date: 2023/6/15 22:19
 * @Description:
 */
@Primary
@Service
@RequiredArgsConstructor
public class DeviceOtaDetailDataImpl implements IDeviceOtaDetailData, IJPACommData<DeviceOtaDetail, Long> {

    private final DeviceOtaDetailRepository deviceOtaDetailRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return deviceOtaDetailRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbDeviceOtaDetail.class;
    }

    @Override
    public Class getTClass() {
        return DeviceOtaDetail.class;
    }
}
