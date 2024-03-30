package cc.iotkit.manager.dto.vo.deviceinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：tfd
 * @Date：2023/6/29 14:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentDeviceVo {

    private String id;

    private String deviceName;
}
