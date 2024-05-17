package com.icia.Taeumproject.Dto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

public class SecurityUserDTO extends User {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final long serialVersionUID = 1L;
    private int M_ID;
    private String M_NAME;
    private String M_PHONE;

    public SecurityUserDTO(MemberDto userDTO) {
        super(userDTO.getUsername(), userDTO.getPassword(), makeGrantedAuthority(userDTO.getRole()));
        this.M_ID = userDTO.getM_ID(); // Assigning M_ID value
        this.M_NAME = userDTO.getM_NAME();
        this.M_PHONE = userDTO.getM_PHONE();
    }

    private static List<GrantedAuthority> makeGrantedAuthority(String role){
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
        return list;
    }
 // Getter method for M_ID
    public int getM_ID() {
        return M_ID;
    }

    // Setter method for M_ID (optional)
    public void setM_ID(int M_ID) {
        this.M_ID = M_ID;
    }
    public String getM_NAME() {
      return M_NAME;
    }
    public void setM_NAME(String M_NAME) {
      this.M_NAME = M_NAME;
    }
    
    public String getM_PHONE() {
      return M_PHONE;
    }
    public void setM_PHONE(String M_PHOME) {
      this.M_PHONE = M_PHOME;
    }
}