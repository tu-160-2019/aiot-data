package net.srt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SqlGeneration
 *
 * @author zrx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlGenerationVo {
    private String flinkSqlCreate;
    private String sqlSelect;
    private String sqlCreate;
}
