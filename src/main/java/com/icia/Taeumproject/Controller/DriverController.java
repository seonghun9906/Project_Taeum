package com.icia.Taeumproject.Controller;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icia.Taeumproject.Dao.NotificationDao;
import com.icia.Taeumproject.Dto.DispatchDto;
import com.icia.Taeumproject.Dto.DriverDto;
import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.Node;
import com.icia.Taeumproject.Dto.NotificationDto;
import com.icia.Taeumproject.Dto.SearchDto;
import com.icia.Taeumproject.Dto.SecurityUserDTO;
import com.icia.Taeumproject.Service.DriverService;
import com.icia.Taeumproject.Service.MainService;
import com.icia.Taeumproject.Service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DriverController {

	@Autowired
	private DriverService drServ;

	@Autowired
	private MemberService mServ;

	@Autowired
	private MainService maServ;
	
	@Autowired
	private NotificationDao nDao;

	// 로그인 후 출력될 기사 메인 화면 이동 및 기사 개인정보 가져오기
	@GetMapping("driverModify")
	public String driverModify(Model model, SearchDto sdto) {
		log.info("driverModify()");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 로그인 시 정보 가져오기
		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		log.info("m_id: {}", m_id);
		
		drServ.getDriverInfo(m_id, model);
		 List<NotificationDto> nList = nDao.selectNotificationList(sdto);
     model.addAttribute("nList", nList);
		return "driverModify";
	}

	// 기사 프로필 이미지 가져오기
	@GetMapping("getDriverImage")
	@ResponseBody
	public String driverProfile(@RequestParam("M_ID") int M_ID, Model model) {
		log.info("driverProfile()");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		log.info("m_id: {}", m_id);

		return drServ.getDriverImage(M_ID, model);

	}

	@GetMapping("driverUpdate")
	public String driverUpdate(Model model, SearchDto sdto) {
		log.info("driverUpdate()");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		log.info("m_id: {}", m_id);

		drServ.getDriverInfo(m_id, model);
		drServ.getDriverImage(m_id, model);
		System.out.println(model);

		List<NotificationDto> nList = nDao.selectNotificationList(sdto);
    model.addAttribute("nList", nList);
		
		return "driverUpdate";
	}

	@PostMapping("driverUpdateProc")
	public String driverUpdateProc(List<MultipartFile> files, DriverDto driver, RedirectAttributes rttr,
	        HttpSession session, Principal principal) {
	    log.info("driverUpdateProc()");
	    String view = null;
	    System.out.println("files = " + files);
	    MemberDto member = new MemberDto();
	    int mid = driver.getM_ID();
	    String m_name = driver.getM_NAME();
	    String m_phone = driver.getM_PHONE();
	    System.out.println(m_phone);
	    member.setM_ID(mid);
	    member.setM_NAME(m_name);
	    member.setM_PHONE(m_phone);
	    // 기사 정보 업데이트
	    mServ.DriveMemberUpdate(member);
	    if(m_phone.equals("null")) {
	    	System.out.println("여기서 걸림");
	    	int dr_id = (mid-1);
	    	drServ.deleteDispatch(dr_id);
	    	drServ.deleteCommute(dr_id);
	    }
	    System.out.println(files.size());
	    	if(!files.isEmpty()) {
	    		// 그 외 업데이트 처리
		    	view = drServ.driverUpdateProc(files, driver, rttr, session, principal);
	    	} else if(!files.isEmpty()) {
	    		// 프로필 이미지 업데이트
		    	drServ.updateDriverProfile(mid);
		    	view = "redirect:/driverModify";
	    	} 

	    return view;
	}

	  @PostMapping("deleteDriverImage")
	  @ResponseBody
	  public void deleteDriverImage(Integer M_ID, String imageNum) {
	    drServ.deleteDriverImage(M_ID, imageNum);
	    FileUtil.deleteFile("webapp/upload/" + imageNum);

	    
	  }
	  // 파일 삭제 메소드
	  private class FileUtil {
	    public static void deleteFile(String filePath) {
	      File file = new File(filePath);
	      if (file.exists()) {
	        if (file.delete()) {
	          System.out.println("파일이 삭제되었습니다: " + filePath);
	        } else {
	          System.out.println("파일 삭제에 실패했습니다: " + filePath);
	        }
	      } else {
	        System.out.println("파일이 존재하지 않습니다: " + filePath);
	      }
	    }
	  }
	
	
// 출퇴근 버튼 클릭 시 값 전송
	@PostMapping("driverCommute")
	@ResponseBody
	public String driverCommute(@RequestParam("status") String status) {
		log.info("driverCommute()");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		log.info("m_id: {}", m_id);

		int dr_id = (m_id - 1);
		log.info("dr_id: {}", dr_id);

		if (status.equals("출근")) {
			
			drServ.insertCommute(m_id, dr_id);
			
		} else if (status.equals("퇴근")) {
			
			drServ.updateCommute(dr_id);
			
		}

		return "redirect:driverModify";
	}

	@GetMapping("/mainCenter")
  public String test(Model model, SearchDto sdto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Object principal = authentication.getPrincipal();
    int m_id = ((SecurityUserDTO) principal).getM_ID();
    int DR_ID = (m_id - 1);

    List<List<Node>> rideNodeList = new ArrayList<>();
    List<Node> innerList1 = new ArrayList<>();
    List<Node> innerList2 = new ArrayList<>();
    List<Node> innerList3 = new ArrayList<>();
    
    List<Node> nodeList = maServ.selectNodeList(DR_ID);

    for (Node node : nodeList) {
      if (node.getCycle() == 1) {
        innerList1.add(node);
      } else if (node.getCycle() == 2) {
        innerList2.add(node);
      } else {
        innerList3.add(node);
      }
    }
    rideNodeList.add(innerList1);
    rideNodeList.add(innerList2);
    rideNodeList.add(innerList3);

    
    
    System.out.println("rideNodeList ==  == = = = =- = = " + rideNodeList);
    model.addAttribute("rideNodeList", rideNodeList);

 
    // model.addAttribute("innerList1", innerList1);
    model.addAttribute("nodeList", nodeList);

    
    List<NotificationDto> nList = nDao.selectNotificationList(sdto);
    model.addAttribute("nList", nList);
    
    return "mainCenter"; // 뷰 이름 반환
  }
	

	@GetMapping("driverAutoJoin")
	public String driverAutoJoin(RedirectAttributes rttr) {
		log.info("driverAutoJoin()");

		for (int i = 2; i <= 4; i++) {
			MemberDto member = new MemberDto();
			member.setUsername("driver" + (i-1) + "@taeum.com");
			member.setPassword("1111");
			member.setM_NAME("드라이버" + (i - 1));
			member.setM_PHONE("null");
			member.setRole("DRIVER");

			mServ.memberJoin(member, rttr);
			int m_id = i;
			mServ.updateRole(m_id);
		}

		for (int i = 1; i <= 3; i++) {
			DriverDto driver = new DriverDto();
			driver.setDR_CARNUM(15 + "더" + (9018 + i));
			driver.setDR_AREA("부평구");
			driver.setM_ID(i + 1);

			drServ.insertDriver(driver, rttr);
		}

		return "redirect:/adminDriverList";
	}

	@PostMapping("/acceptNode")
	public String acceptNode(@RequestBody List<Node> data) {
		log.info("acceptNode()");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		int DR_ID = (m_id - 1);

		System.out.println("여기지롱" + data);
		drServ.updateConfirm(data, DR_ID);
		return "mainCenter"; // 적절한 응답 처리
	}

	@PostMapping("/deniedNode")
	public String deniedNode(@RequestBody List<DispatchDto> dataToSend) {
		log.info("deniedNode()");

		// 사용자 정보 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		int m_id = ((SecurityUserDTO) principal).getM_ID();
		int DR_ID = (m_id - 1);

		drServ.updateCancle(dataToSend, DR_ID);

		return "mainCenter"; // 적절한 응답 처리

	}
	
	
	
}
