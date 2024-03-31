package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbOtaPackage;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OtaPackageMapper extends BaseMapper<TbOtaPackage> {
}
