package cc.iotkit.common.api;

import lombok.Data;

/**
 * @author: Longjun.Tu
 * @description:
 * @date:created in 2023/5/10 23:16
 * @modificed by:
 */
@Data
public class PageRequestEmpty {
  private Integer pageNum = 1;
  private  Integer pageSize = 20;
}
