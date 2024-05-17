package com.icia.Taeumproject.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icia.Taeumproject.Service.ApplyService;
import com.icia.Taeumproject.Service.MailService;
import com.icia.Taeumproject.Service.MemberService;
import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.SearchDto;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;



@RestController
@Slf4j
public class MemberRestController {
  @Autowired
  MemberService mServ;
  
  @Autowired
  MailService mailServ;
  
  @Autowired
  ApplyService aServ;

  @GetMapping("emailCheck")

  public String emailCheck(@RequestParam("username") String username) {
    log.info("emailCheck()");
    String res = mServ.emailCheck(username);
    return res;
  }
  

  @PostMapping("mailConfirm")
  public String mailConfirm(MemberDto member, HttpSession session) {
    log.info("mailConfirm()");
    String res = mailServ.sendEmail(member, session);
    return res;
  }
  
  @PostMapping("codeAuth")
  public String codeAuth(@RequestParam("v_code") String v_code,
               HttpSession session) {
    log.info("codeAuth()");
    String res = mailServ.codeAuth(v_code, session);
    return res;
  }
  
  
}
