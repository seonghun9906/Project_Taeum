package com.icia.Taeumproject.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDto {

	 private int C_ID;
	 private int B_ID; 
	 private String C_CONTENTS;
	 private Timestamp C_DATE;
	 private int M_ID;
	
}
