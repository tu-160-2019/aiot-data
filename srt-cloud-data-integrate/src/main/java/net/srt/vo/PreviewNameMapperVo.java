package net.srt.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreviewNameMapperVo {

  private String originalName;
  private String targetName;
  private String remarks;
}
