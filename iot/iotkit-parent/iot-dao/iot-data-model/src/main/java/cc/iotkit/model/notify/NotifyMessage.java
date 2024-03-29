package cc.iotkit.model.notify;

import cc.iotkit.model.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 石恒
 * @Date: 2023/5/13 15:22
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessage implements Id<Long> {

    private Long id;

    private String content;

    private String messageType;

    private Boolean status;

    private Long createAt;

    private Long updateAt;
}
