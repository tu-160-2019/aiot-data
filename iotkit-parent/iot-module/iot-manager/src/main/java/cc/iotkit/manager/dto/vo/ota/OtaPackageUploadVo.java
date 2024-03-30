package cc.iotkit.manager.dto.vo.ota;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 石恒
 * @Date: 2023/6/27 22:35
 * @Description:
 */
@Data
@ExcelIgnoreUnannotated
@ApiModel(value = "OtaPackageUploadVo")
public class OtaPackageUploadVo implements Serializable {
    private String url;
    private Long size;
    private String md5;
    private Long ossId;
    private String originalName;
}
