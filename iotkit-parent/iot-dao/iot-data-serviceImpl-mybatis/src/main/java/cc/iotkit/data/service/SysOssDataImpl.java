package cc.iotkit.data.service;

import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysOssRepository;
import cc.iotkit.data.model.TbSysOss;
import cc.iotkit.data.system.ISysOssData;
import cc.iotkit.model.system.SysOss;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysOssDataImpl implements ISysOssData, IJPACommData<SysOss, Long> {

    @Autowired
    private SysOssRepository baseRepository;

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public JpaRepository getBaseRepository() {
        return baseRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysOss.class;
    }

    @Override
    public Class getTClass() {
        return SysOss.class;
    }


}
