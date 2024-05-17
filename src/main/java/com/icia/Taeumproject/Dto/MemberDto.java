package com.icia.Taeumproject.Dto;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
    private int M_ID;
    private String username;
    private String password;
    private String M_NAME;
    private String M_PHONE;
    private String role;
}
