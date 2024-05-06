package com.ai.tts.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.mybatis.entity.BaseEntity;
import com.ai.tts.vo.DataTtsVo;

@EqualsAndHashCode(callSuper = false)
@AutoMapper(target = DataTtsVo.class)
@Data
@TableName("data_tts")
public class DataTtsEntity extends BaseEntity {
    private String text;
    private String ttsPath;
    private String fileUrl;
    private Integer type;
    private Long orgId;
    private Long size;

}
