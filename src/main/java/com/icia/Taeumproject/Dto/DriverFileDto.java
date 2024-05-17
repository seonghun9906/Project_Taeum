package com.icia.Taeumproject.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DriverFileDto {
	private int DP_ID;
	private int M_ID;//외래키 드라이버 ID
	private String DP_SYSNAME;
	private String DP_ORINAME;
}