package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.manager.dto.bo.plugin.PluginInfoBo;
import cc.iotkit.manager.dto.vo.plugin.PluginInfoVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author sjg
 */
public interface IPluginService {

    /**
     * 上传jar包
     *
     * @param file 文件
     * @param id   插件id
     */
    void upload(MultipartFile file, Long id);

    /**
     * 添加插件
     *
     * @param plugin 插件信息
     */
    void addPlugin(PluginInfoBo plugin);

    /**
     * 修改插件信息
     *
     * @param plugin 插件信息
     */
    void modifyPlugin(PluginInfoBo plugin);

    /**
     * 获取插件信息
     *
     * @param id 插件id
     * @return 插件信息
     */
    PluginInfoVo getPlugin(Long id);

    /**
     * 删除插件
     *
     * @param id 插件id
     */
    void deletePlugin(Long id);

    /**
     * 分页查询
     *
     * @param query 查询条件
     */
    Paging<PluginInfoVo> findPagePluginList(PageRequest<PluginInfoBo> query);

    /**
     * 修改插件状态
     *
     * @param plugin 插件信息
     */
    void changeState(PluginInfoBo plugin);
}
