package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.CategoryMapper;
import cc.iotkit.data.model.TbCategory;
import cc.iotkit.data.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, TbCategory> implements CategoryService {
}
