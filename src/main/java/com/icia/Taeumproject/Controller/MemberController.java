package com.icia.Taeumproject.Controller;


import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icia.Taeumproject.Dao.NotificationDao;
import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.NotificationDto;
import com.icia.Taeumproject.Dto.SearchDto;
import com.icia.Taeumproject.Dto.SecurityUserDTO;
import com.icia.Taeumproject.Service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j

public class MemberController {

  @Autowired
  MemberService mServ; 
  
  @Autowired
  NotificationDao nDao;

  @GetMapping("/")
  public String home() {
    log.info("/");
   
    return "home";
  }

  @GetMapping("joinForm")
  public String joinForm() {
    log.info("joinForm");

    return "joinForm";
  }

  @PostMapping("joinProc")
  public String joinProc(MemberDto member, RedirectAttributes rttr ) {

    log.info("joinProc()");
    String view = mServ.memberJoin(member, rttr);

    return view;
  }
  @GetMapping("loginForm")
  public String loginForm() {
    log.info("loginForm()");
    return "loginForm";
  } 
  @PostMapping("loginProc")
  public String loginProc(MemberDto member , Model model, RedirectAttributes rttr) {
   log.info("loginProc()");
     return mServ.loginProc(model,rttr, member);
  }
  @GetMapping("loginChange")
  public String loginChange(Model model, SearchDto sdto) {
	  log.info("loginChange()");
	  
	   List<NotificationDto> nList = nDao.selectNotificationList(sdto);
	    model.addAttribute("nList", nList);
	  
	  return "loginChange";
  }
  
  @PostMapping("loginChangeProc")
  public String loginChangeProc(@RequestParam("num") int num, MemberDto member, Model model, RedirectAttributes rttr) {
      log.info("loginChangeProc()");
      String username = null;
      String password = null;
      String role = null;
      
      if(num == 1) {
          username = "admin@taeum.com";
          password = "1111";
          role = "ADMIN";
      } else if(num == 2) {
    	  username = "driver1@taeum.com";
          password = "1111";
          role = "DRIVER";
      } else if(num == 3) {
    	  username = "driver2@taeum.com";
          password = "1111";
          role = "DRIVER";
      } else {
    	  username = "driver3@taeum.com";
          password = "1111";
          role = "DRIVER";
      }
      member.setUsername(username);
      member.setPassword(password);
      member.setRole(role);
      System.out.println(member);
    
      return mServ.loginProc(model, rttr, member);
  }
  
//메일 인증 메핑 메소드
  @GetMapping("authUser")
  public String authUser() {
    log.info("authUser()");
    return "authUser";
  }
  // 비밀번호 변경
  @GetMapping("pwdChange")
  public String pwdChange() {
    log.info("pwdChange()");
    return "pwdChange";
  }
  
  @PostMapping("pwdChangeProc")
  public String pwdChangeProc(RedirectAttributes rttr,String password, String userName) {
	 log.info("pwdChangeProc()");
	 mServ.pwdChangeProc(rttr,password,userName);
	 
	 return "redirect:/loginForm";
  }
  
  @GetMapping("findEmail")
  public String findEmail() {
    log.info("findEmail()");
    
    return "findEmail";
  }
  
  @PostMapping("findById")
  public String findById(String m_name,String m_phone,RedirectAttributes rttr) {
    log.info("findById()");
    log.info(m_name);
    
    return mServ.findById(m_name,m_phone,rttr);
  }
  
  
  @GetMapping("userUpdate")
  public String UserUpdate() {
	  log.info("UserUpdate()");
	  
	  return "/userUpdate";
  }
  
  // 사용자 정보수정
  @PostMapping("UserUpdateProc")
  public String UserUpdateProc(@RequestParam("M_NAME") String M_NAME,
		  		@RequestParam("M_PHONE") String M_PHONE, Principal principal,Model model,RedirectAttributes rttr,HttpSession session) {
	  log.info("UserUpdateProc()");
	  
	  // Spring Security를 사용하여 현재 로그인한 사용자 정보 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
	  
	    
	  return mServ.userUpdate(M_NAME,M_PHONE,principal,rttr,session);
  }
  // 탈퇴
  @GetMapping("withDrawal")
  	public String withDrawal(RedirectAttributes rttr,HttpSession session) {
	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		log.info("m_id: {}", m_id);
	  	mServ.withDrawal(m_id,rttr,session);
	  
	  return "redirect:/";
  }
  
	/*
	 * @GetMapping("deleteMember") public String deleteMember() { Authentication
	 * authentication = SecurityContextHolder.getContext().getAuthentication();
	 * 
	 * // 로그인 시 정보 가져오기 Object principal = authentication.getPrincipal(); int m_id =
	 * ((SecurityUserDTO) principal).getM_ID(); log.info("m_id: {}", m_id);
	 * 
	 * mServ.withDrawal(m_id,rttr,session); }
	 */
  

}




















