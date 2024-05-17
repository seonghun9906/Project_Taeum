package com.icia.Taeumproject.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icia.Taeumproject.Dao.ApplyDao;
import com.icia.Taeumproject.Dao.MainDao;
import com.icia.Taeumproject.Dao.MemberDao;
import com.icia.Taeumproject.Dao.NotificationDao;
import com.icia.Taeumproject.Dto.ApplyDto;
import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.Node;
import com.icia.Taeumproject.Dto.NotificationDto;
import com.icia.Taeumproject.Dto.SearchDto;
import com.icia.Taeumproject.util.KakaoApiUtil;
import com.icia.Taeumproject.util.KakaoApiUtil.Point;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplyService {
  @Autowired
  private ApplyDao aDao;
  @Autowired
  private MemberDao mDao;

  @Autowired
  private NotificationDao nDao;

  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public ApplyService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Autowired
  private MainDao maDao;

  @Autowired
  private MemberService memberService;
  @Autowired
  private MainService maServ;
//transaction 관련
  @Autowired
  private PlatformTransactionManager manager;
  @Autowired
  private TransactionDefinition definition;

  public void updateApplyStatusWithNodeList(SearchDto sdto, int m_id, int page, int size, Model model) {
	   
		// 노드 리스트 가져오기
	    List<Node> nodeList = maDao.getNodeList(m_id);

	    int offset = (page - 1) * size;
	    
	    // 신청 리스트 가져오기
	    List<ApplyDto> applyList = aDao.getApplyList(m_id, offset, size);

	    // 노드 내역을 맵으로 변환하여 효율적으로 검색하기
	    Map<String, Integer> nodeStatusMap = new HashMap<>(); // 상태를 정수로 저장할 맵으로 수정
	    for (Node node : nodeList) {
	      String key = node.getM_ID() + "-" + node.getA_DATE(); // M_ID와 A_DATE를 조합하여 고유한 키 생성
	      // 노드 내역의 M_ID와 A_DATE를 키로, 상태를 정수로 변환하여 맵에 추가
	      int status = 0; // 기본값 설정
	      if (node.getStatus() != null && !node.getStatus().isEmpty()) {
	        try {
	          status = Integer.parseInt(node.getStatus());
	        } catch (NumberFormatException e) {
	          // 예외 처리
	          e.printStackTrace(); // 혹은 다른 처리 방법을 선택
	        }
	      }
	      nodeStatusMap.put(key, status);
	    }
	    // 신청 리스트의 각 항목에 대해 처리
	    for (ApplyDto apply : applyList) {
	      String key = apply.getM_ID() + "-" + apply.getA_DATE(); // 신청 내역의 M_ID와 A_DATE를 조합하여 키 생성
	      if (nodeStatusMap.containsKey(key)) { // 해당하는 노드 내역이 있는 경우
	        // 해당 신청 내역의 status 값을 노드 내역의 status 값으로 설정
	        apply.setSTATUS(nodeStatusMap.get(key));
	      }
	    }
	    int applyCnt = aDao.selectAplCnt(sdto);
	    
	    int totalPages = (int) Math.ceil((double) applyCnt / size);
	    int prevPage = Math.max(page - 1, 1);
	    int nextPage = Math.min(page + 1, totalPages);
	    
	    // 처리된 신청 리스트를 모델에 추가
	    model.addAttribute("applyList", applyList);
	    model.addAttribute("applyCount", applyCnt); // 전체 신청 수
	    model.addAttribute("totalPages", totalPages); // 전체 페이지 수
	    model.addAttribute("prevPage", prevPage);
	    model.addAttribute("nextPage", nextPage);
	    
	  }


//게시글 , 회원가입
  @Transactional
  public String applyWrite(ApplyDto apply, RedirectAttributes rttr, String username) {
    log.info("applyWriteapplyWriteapplyWriteapplyWriteapplyWrite =  " + apply.getA_LOCALDATE());
    // 현재 로그인한 사용자 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String view = null;
    String msg = null;

    try {
      // 중복 체크: 이미 해당 시간에 같은 탑승자의 신청이 있는지 확인
      if (isDuplicateApply(apply.getM_ID() ,apply.getA_NAME(), apply.getA_DATE())) {
        msg = "이미 해당 시간에 같은 탑승자의 신청이 있습니다.";
        rttr.addFlashAttribute("msg", msg);
        view = "redirect:/";

      } else {
        System.out.println(" apply.getA_DATE()apply.getA_DATE()apply.getA_DATE() = " + apply.getA_LOCALDATE());

        // 게시글 저장
        apply.setSTATUS(0); // 0 배차중, 1 수락, 2 거절
        aDao.insertApply(apply);
     
        // 발신자 정보 조회
        MemberDto user = mDao.findUserByEmail(username);

        // 알림 메시지 생성
        String notificationMessage = user.getM_NAME() + "님이 새로운 신청서를 작성하였습니다.";

        // 메시지를 DB에 저장
        saveNotificationMessageToDB(notificationMessage);

        // WebSocket을 통해 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications", notificationMessage);

        if (apply.getA_STARTADRESS() != null && !apply.getA_STARTADRESS().isEmpty()) {
          Point startPoint = KakaoApiUtil.getPointByAddress(apply.getA_STARTADRESS());

          if (startPoint != null) {
            int kind = 1;
            maServ.insertServ(apply.getM_ID(), apply.getA_STARTADRESS(), startPoint, kind, rttr, 
                apply.getA_DATE(), apply.getM_NAME(), apply.getM_PHONE(), apply.getA_NAME(), apply.getA_CONTENTS(), apply.getA_ID());

          }
        }
        if (apply.getA_ENDADRESS() != null && !apply.getA_ENDADRESS().isEmpty()) {
          Point endPoint = KakaoApiUtil.getPointByAddress(apply.getA_ENDADRESS());

          if (endPoint != null) {
            int kind = 2;
            maServ.insertServ(apply.getM_ID(), apply.getA_ENDADRESS(), endPoint, kind, rttr, 
                apply.getA_DATE(),apply.getM_NAME(), apply.getM_PHONE(), apply.getA_NAME(), apply.getA_CONTENTS(), apply.getA_ID());
          }
        }

        log.info("apply.getA_DATE(): {}", apply.getA_DATE());

        // 사용자 정보 업데이트
        mDao.findUserByEmail(username);
        ;
        view = "redirect:/";
        msg = "작성 성공";
      }
    } catch (Exception e) {
      e.printStackTrace();
      view = "redirect:/applyForm";
      msg = "작성 실패";
    }

    // 리다이렉트된 페이지에서 사용할 수 있도록 Flash Attribute로 메시지 전달
    rttr.addFlashAttribute("msg", msg);

    return view;
  }

  // 신청 중복 확인
  private boolean isDuplicateApply(int M_ID,String A_NAME ,String a_DATE) {

    log.info("Formatted Date: {}", a_DATE);
    // DAO를 통해 변환된 문자열과 다른 파라미터들로 데이터베이스에서 중복 체크 수행
    return aDao.sDuplicateApply(M_ID, A_NAME ,a_DATE);
  }

  public String getApply(int A_ID, Model model) {
    log.info("getApply()");
    ApplyDto apply = aDao.selectApplyCnt(A_ID);
    model.addAttribute("apply", apply);
    return "applyDetail";
  }

  // 신청 삭제
  public void cancelApply(int A_Id, int M_Id, String A_NAME ,String A_Date) {
    log.info("cancelApply");
    aDao.cancelApply(A_Id);
    maDao.cancelNode(M_Id,A_NAME,A_Date);
  

  }

  private void saveNotificationMessageToDB(String notificationMessage) {
    NotificationDto notification = new NotificationDto();
    notification.setRole("ROLE_USER"); // 예시로 USER 역할로 설정
    notification.setMessage(notificationMessage);
    nDao.insertNotification(notification);
  }

  public String popList(SearchDto sdto, HttpSession session, Model model) {

    log.info("popList()");

    String view = "Popup";

    // 알림 목록 가져오기
    List<NotificationDto> nList = nDao.selectNotificationList(sdto);
   
    // 모델에 알림 목록 추가
    model.addAttribute("nList", nList);

    return view;
  }
  public void deletePopup(@RequestParam("NOTIFICATION_ID") int NOTIFICATION_ID) {
      log.info("deletePopup()");
    
    // 팝업 메시지 삭제
    nDao.deleteNotification(NOTIFICATION_ID);
}
  public List<ApplyDto> selectAllMember() {
	  List<ApplyDto> memberList = aDao.selectAllMember();
	      return memberList;
	    }
  
  

}

