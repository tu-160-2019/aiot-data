package cc.iotkit.temporal.ts.dm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbField {
    private String name;
    private String type;

    private int length;
}