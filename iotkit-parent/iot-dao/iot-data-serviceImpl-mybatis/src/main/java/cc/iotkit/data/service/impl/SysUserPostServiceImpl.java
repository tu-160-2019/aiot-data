package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysUserPostMapper;
import cc.iotkit.data.model.TbSysUserPost;
import cc.iotkit.data.service.SysUserPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class SysUserPostServiceImpl extends ServiceImpl<SysUserPostMapper, TbSysUserPost> implements SysUserPostService {
}
