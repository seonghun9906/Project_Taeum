package com.icia.Taeumproject.Dto;

import lombok.Data;


@Data
public class DispatchDto {
  private Long D_ID;
  private String D_SELECT;
  private String D_STATUS;
  private String D_CONFIRM;
  private String D_CANCLE;
  private String D_REASON;
  private int DR_ID;
  private String D_DATE;
  private int cycle;

  
}
