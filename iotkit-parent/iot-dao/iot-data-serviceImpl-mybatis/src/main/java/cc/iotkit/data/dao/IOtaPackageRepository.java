package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbOtaPackage;
import cc.iotkit.model.ota.OtaPackage;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOtaPackageRepository extends BaseMapper<TbOtaPackage> {
    List<OtaPackage> findByVersionGreaterThan(String version);
}
