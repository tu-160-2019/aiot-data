package com.ai.tts.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.utils.DateUtils;
import net.srt.framework.mybatis.entity.BaseEntity;

import java.io.Serializable;
import java.util.Date;

@Data
public class DataTtsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String text;
    private String ttsPath;
    private String fileUrl;
    private Integer type;
    private Long orgId;
    private Double size;
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date createTime;

}
