package cc.iotkit.data.service;

import cc.iotkit.data.model.TbOtaPackage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OtaPackageService extends IService<TbOtaPackage> {

    Page<TbOtaPackage> findAll(int page, int size);
}