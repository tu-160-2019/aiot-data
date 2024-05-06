package com.ai.tts.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;
import net.srt.framework.common.utils.DateUtils;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataTtsQuery extends Query {
    private static final long serialVersionUID = 1L;
    private String text;
    private String ttsPath;
    private String fileUrl;
    private Integer type;
    private Long orgId;
    private Long size;
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date createTime;
}
