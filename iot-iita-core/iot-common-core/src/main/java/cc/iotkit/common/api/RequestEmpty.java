package cc.iotkit.common.api;

import cn.hutool.core.util.IdUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: Longjun.Tu
 * @description:
 * @date:created in 2023/5/10 23:12
 * @modificed by:
 */
@Data
public class RequestEmpty implements Serializable {

  @NotBlank
  private String requestId;

  public static RequestEmpty of() {
    RequestEmpty request = new RequestEmpty();
    request.setRequestId(IdUtil.simpleUUID());
    return request;
  }

}
