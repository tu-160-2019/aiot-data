package cc.iotkit.screen.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：tfd
 * @Date：2023/6/25 15:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpContent {
    private String content;
}
