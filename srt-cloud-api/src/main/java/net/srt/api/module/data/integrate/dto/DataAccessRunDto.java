package net.srt.api.module.data.integrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DataAccessRunDto
 * @Author zrx
 * @Date 2023/11/16 22:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataAccessRunDto {
	private Long dataAccessId;
	private Integer runType;
	private String executeNo;
}
