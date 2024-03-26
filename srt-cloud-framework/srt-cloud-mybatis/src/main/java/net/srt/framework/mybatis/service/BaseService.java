package net.srt.framework.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;

/**
 * 基础服务接口，所有Service接口都要继承
 *
 * @author 阿沐 babamu@126.com
 */
public interface BaseService<T> extends IService<T> {


	Long getProjectId();

	DataProjectCacheBean getProject();
}
