package com.icia.Taeumproject.Service;

import java.io.File;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icia.Taeumproject.Dao.DriverDao;
import com.icia.Taeumproject.Dto.DispatchDto;
import com.icia.Taeumproject.Dto.DriverDto;
import com.icia.Taeumproject.Dto.DriverFileDto;
import com.icia.Taeumproject.Dto.Node;
import com.icia.Taeumproject.Dto.SecurityUserDTO;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DriverService {
	@Autowired
	private PlatformTransactionManager manager;
	@Autowired
	private TransactionDefinition definition;
	@Autowired
	private DriverDao drDao;
	@Autowired
    private CustomUserDetailsService customUserDetailsService;

	public void getDriverInfo(int m_id, Model model) {
		log.info("getDriverInfo()");

		DriverDto drDto = drDao.getDriverInfo(m_id);
		System.out.println(m_id);
		model.addAttribute("driver", drDto);
		System.out.println(drDto);

		// 오늘 운행 건수 가져오기
		int todayTraffic = drDao.getTodayTraffic(m_id);
		model.addAttribute("todayTraffic", todayTraffic);

		// 이번 달 운행 건수 가져오기
		int monthTraffic = drDao.getMonthTraffic(m_id);
		model.addAttribute("monthTraffic", monthTraffic);

		// 총 운행 건수 가져오기
		int totalTraffic = drDao.getTotalTraffic(m_id);
		model.addAttribute("totalTraffic", totalTraffic);

		String driverImage = drDao.getDriverImage(m_id);
		model.addAttribute("driverImage", driverImage);
	}


	public String getDriverImage(int M_ID, Model model) {
		String driverImage = drDao.getDriverImage(M_ID);
		model.addAttribute("driverImage", driverImage);

		log.info("driverImage: {}", driverImage);

		return driverImage;
	}

	public String insertDriver(DriverDto driver, RedirectAttributes rttr) {
		log.info("insertDriver()");
		String view = null;
		String msg = null;

		try {
			drDao.insertDriver(driver);
			log.info("driver: {}", driver);
			view = "redirect:adminMain";
			msg = "저장 성공";

		} catch (Exception e) {
			e.printStackTrace();
			view = "redirect:adminMain";
			msg = "저장 실패";
		}

		rttr.addFlashAttribute("msg", msg);

		return view;
	}

	private void fileUpload(List<MultipartFile> files, HttpSession session, int M_ID) throws Exception {
//파일 저장 실패 시 데이터베이스 롤백작업이 이루어지도록 예외를 throws 할 것.
		log.info("fileUpload()");
		drDao.updateDriverProfile(M_ID);
//파일 저장 위치 처리(session에서 저장 경로를 구함)
		String realPath = session.getServletContext().getRealPath("/");
		log.info("realPath : {}", realPath);

		realPath += "upload/";// 파일 업로드 폴더

		File folder = new File(realPath);
		if (folder.isDirectory() == false) {
			folder.mkdir();// 폴더 생성 메소드
		}

		for (MultipartFile mf : files) {
			// 파일명 추출
			String oriname = mf.getOriginalFilename();

			DriverFileDto dfd = new DriverFileDto();
			dfd.setDP_ORINAME(oriname);
			dfd.setM_ID(M_ID);
			String sysname = System.currentTimeMillis() + oriname.substring(oriname.lastIndexOf("."));
			// 확장자 : 파일을 구분하기 위한 식별 체계. (예. xxxx.jpg)
			dfd.setDP_SYSNAME(sysname);

			// 파일 저장
			File file = new File(realPath + sysname);
			mf.transferTo(file);

			// 파일 정보 저장
			drDao.insertFile(dfd);
		}
	}

	public String driverUpdateProc(List<MultipartFile> files, DriverDto driver, RedirectAttributes rttr, HttpSession session, Principal principal) {
	    log.info("driverUpdateProc()");
	    TransactionStatus status = manager.getTransaction(definition);
	    String msg = null;
	    
	    try {
	        // 드라이버 정보 업데이트
	        drDao.updateDriver(driver);
	        msg = "업데이트 완료";

	        if (!files.get(0).isEmpty()) {// 업로드 파일이 있다면
	            fileUpload(files, session, driver.getM_ID()); // 여기는 컬럼을 어떻게할지 정해야함
	        }

	        // commit 수행
	        manager.commit(status);

	        // 인증 정보 다시 저장
	        Authentication authentication = (Authentication) principal;
	        UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(authentication.getName());
	        Authentication updatedAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, authentication.getCredentials(), updatedUserDetails.getAuthorities());
	        SecurityContextHolder.getContext().setAuthentication(updatedAuth);

	    } catch (Exception e) {
	        e.printStackTrace();
	        manager.rollback(status);
	        msg = "업데이트 실패";
	    }

	    rttr.addFlashAttribute("msg", msg);
	    return "redirect:/driverModify";
	}

	public void updateDriverProfile(int mid) {
		log.info("updateDriverProfile()");
		drDao.updateDriverProfile(mid);
	}

	public void updateConfirm(List<Node> data, int DR_ID) {
		log.info("updateStatus()");

		for (Node node : data) {
			DispatchDto dispatch = new DispatchDto();
			long D_ID = node.getD_ID();
			dispatch.setD_ID(D_ID);
			dispatch.setDR_ID(DR_ID);

			drDao.updateConfirm(dispatch);
			drDao.updateNodeConfirm(dispatch);
		}
	}

	public void updateCancle(List<DispatchDto> dispatchDto,int DR_ID) {
		log.info("updateCancle()");
		
		for (DispatchDto dispatch : dispatchDto) {
			long D_ID = dispatch.getD_ID();
			String D_REASON = dispatch.getD_REASON();
			dispatch.setD_ID(D_ID);
			dispatch.setDR_ID(DR_ID);
			dispatch.setD_REASON(D_REASON);
			dispatch.setCycle(0);
			
			drDao.updateCancle(dispatch);
			drDao.updateNodeCancle(dispatch);
		}
	}

	public void insertCommute(int m_id, int dr_id) {
		
		drDao.insertCommute(m_id, dr_id);
	}

	public void updateCommute(int dr_id) {
		
		drDao.updateCommute(dr_id);
	}


  public void deleteDriverImage(Integer m_ID, String imageNum) {
   drDao.deleteDriverImage(m_ID, imageNum);
  }


  public void deleteDispatch(int dr_id) {
	  drDao.deleteDispatch(dr_id);
 }


  public void deleteCommute(int dr_id) {
	  drDao.deleteCommute(dr_id);
}


}
