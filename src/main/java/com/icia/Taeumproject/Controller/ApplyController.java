package com.icia.Taeumproject.Controller;


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
import com.icia.Taeumproject.Dto.ApplyDto;
import com.icia.Taeumproject.Dto.SearchDto;
import com.icia.Taeumproject.Dto.SecurityUserDTO;
import com.icia.Taeumproject.Service.ApplyService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;



@Controller
@Slf4j
public class ApplyController {
      @Autowired
      private ApplyService aServ;
      @Autowired
      private  NotificationDao nDao;
    
      
      @GetMapping("applyList")
  		public String applyList(SearchDto sdto,
  								@RequestParam(name = "page", defaultValue = "1") int page,
  								@RequestParam(name = "size", defaultValue = "3") int size,
  								Model model) {
  		log.info("applyList()");
  		
  		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

  		// 로그인 시 정보 가져오기
  		Object principal = authentication.getPrincipal();
  		int m_id = ((SecurityUserDTO) principal).getM_ID();
  		log.info("m_id: {}", m_id);

  		
  		// aServ.getApplyList(m_id, model);
  		aServ.updateApplyStatusWithNodeList(sdto, m_id, page, size, model);
  		
  		return "applyList";
  		}

      
     @GetMapping("applyForm")
     public String ApplyForm() {
       
         return "applyForm";
     }
     
     @PostMapping("ApplyProc")
     public String ApplyProc(ApplyDto apply, RedirectAttributes rttr) {
         log.info("ApplyProcApplyProcApplyProcApplyProcApplyProc  = " + apply);
         
         // 현재 로그인한 사용자 정보 가져오기
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName(); // 현재 로그인한 사용자의 아이디
         
         String view = aServ.applyWrite(apply, rttr, username);
         
        // 만약 글 작성이 성공하고, 현재 로그인한 사용자의 역할이 USER라면 알림 전송
         if (view.equals("redirect:/")) {
            
         }
         
         return "redirect:/applyList";
     }
     
     @PostMapping("/cancelApply")
     public String cancelApply(@RequestParam("A_Id") int A_Id,
    		 		@RequestParam("M_Id") int M_Id,
    		 			@RequestParam("A_Date") String A_Date,
    		 			@RequestParam("A_NAME") String A_NAME,
    		 				RedirectAttributes redirectAttributes) {
         log.info("cancelApply");

         aServ.cancelApply(A_Id,M_Id,A_NAME,A_Date);
         return "redirect:/"; // 홈으로 리다이렉트
     }
     
     @GetMapping("popup")
     public String pop(SearchDto sdto , HttpSession session , Model model) {
       
       log.info("pop()");
       
       String view = aServ.popList(sdto,session,model);
       
       
       return view;
     }
     @PostMapping("/deletePop")
     public String deletePopup(@RequestParam("NOTIFICATION_ID") int NOTIFICATION_ID, RedirectAttributes rttr) {
         log.info("deletePopup");
         System.out.println("NOTIFICATION_ID = "  + NOTIFICATION_ID);
        

         // 팝업 메시지 삭제 서비스 메소드 호출
         aServ.deletePopup(NOTIFICATION_ID);

         // 삭제 후 리다이렉트
         return "redirect:/popup";
     }
     
     @PostMapping("alldeletePop")
     public String alldeletePop() {
       nDao.deleteAllNotifications();
       
       return "redirect:/popup";
     }
 }
     

    