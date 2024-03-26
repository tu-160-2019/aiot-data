package net.srt.api.module.data.integrate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreviewNameMapperDto {

	private String originalName;
	private String targetName;
	private String remarks;
}
