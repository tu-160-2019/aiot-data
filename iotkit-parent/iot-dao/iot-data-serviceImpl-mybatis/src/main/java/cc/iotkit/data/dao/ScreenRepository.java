package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbScreen;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:23
 */
@Mapper
public interface ScreenRepository extends BaseMapper<TbScreen> {

    TbScreen findByIsDefault(boolean isDefault);

    List<TbScreen> findByState(String state);
}
