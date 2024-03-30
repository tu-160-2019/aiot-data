package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbScreenApi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:23
 */
@Mapper
public interface ScreenApiRepository extends BaseMapper<TbScreenApi> {

    List<TbScreenApi> findByScreenId(Long screenId);

    void deleteByScreenId(Long screenId);
}
