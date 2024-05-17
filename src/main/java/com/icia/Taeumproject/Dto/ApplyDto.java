package com.icia.Taeumproject.Dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplyDto {
  private String M_NAME;
  private String M_PHONE;
  private int A_ID;
  private String A_NAME;
  private String A_PHONE;
  private String A_STARTADRESSNUM;
  private String A_STARTADRESS; 
  private String A_STARTDADRESS;
  private String A_STARTRADRESS;
  private String A_ENDADRESSNUM;
  private String A_ENDADRESS;
  private String A_ENDDADRESS;
  private String A_ENDRADRESS;
  private String A_DATE;
  private Timestamp A_LOCALDATE;
  private String A_CONTENTS;
  private int M_ID;
  private Integer STATUS;
  
}




