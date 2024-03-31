package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbSysApp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用信息对象 SYS_APP
 *
 * @author tfd
 * @date 2023-08-10
 */
@Mapper
public interface SysAppMapper extends BaseMapper<TbSysApp> {

}