package com.icia.Taeumproject.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardDto {
		
	 private int B_ID;
	 private String username; // 작성자 이름
	 private String B_TITLE;
	 private String B_CONTENTS;
	 private Timestamp B_DATE;
	 private int M_ID;
	 private String role;
}
