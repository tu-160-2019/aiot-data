package cc.iotkit.system.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.system.dto.bo.SysOssBo;
import cc.iotkit.system.dto.vo.SysOssVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传 服务层
 *
 * @author Lion Li
 */
public interface ISysOssService {

    Paging<SysOssVo> queryPageList(PageRequest<SysOssBo> query);

    List<SysOssVo> listByIds(Collection<Long> ossIds);

    SysOssVo getById(Long ossId);

    SysOssVo upload(MultipartFile file);

    void download(Long ossId) throws IOException;

    void deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
