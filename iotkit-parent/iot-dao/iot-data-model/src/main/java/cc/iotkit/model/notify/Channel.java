package cc.iotkit.model.notify;

import cc.iotkit.model.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: 石恒
 * date: 2023-05-11 16:30
 * description:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel implements Id<Long> {

    private Long id;

    private String code;

    private String title;

    private String icon;

    private Long createAt;

}
