package com.icia.Taeumproject.Service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icia.Taeumproject.Dao.ApplyDao;
import com.icia.Taeumproject.Dao.BoardDao;
import com.icia.Taeumproject.Dao.MainDao;
import com.icia.Taeumproject.Dao.MemberDao;
import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.SecurityUserDTO;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {
	@Autowired
	private MemberDao mDao;
	@Autowired
	private ApplyDao aDao;
	@Autowired
	private BoardDao bDao;
	@Autowired
	private MainDao maDao;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	// 비밀번호 암호와 인코더
	private BCryptPasswordEncoder pEncoder = new BCryptPasswordEncoder();
	@Autowired
	private PasswordEncoder passwordEncoder;

//로그인 처리 메소드
	public String loginProc(Model model, RedirectAttributes rttr, MemberDto member) {
		System.out.println(member);
		log.info("loginProc()");
		String view = null;
		String msg = null;

		// 사용자 입력 이메일로 UserDetails 가져오기
		UserDetails userDetails;
		try {
			userDetails = customUserDetailsService.loadUserByUsername(member.getUsername());
		} catch (UsernameNotFoundException e) {
			msg = "해당 이메일을 가진 사용자를 찾을 수 없습니다.";
			model.addAttribute("msg", msg);
			return "loginForm"; // 사용자를 찾을 수 없으면 다시 로그인 폼으로 이동
		}

		// 입력한 비밀번호와 UserDetails에서 가져온 암호화된 비밀번호 비교
		if (passwordEncoder.matches(member.getPassword(), userDetails.getPassword())) {
			// 인증 성공
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication); // Spring Security에 인증 정보 저장
			msg = "로그인에 성공했습니다.";
			if (member.getRole() == "ADMIN") {
				view = "redirect:/adminMain";
			} else {
				view = "redirect:/driverModify"; // 로그인 성공 시 홈페이지로 리다이렉트
			}
		} else {
			// 비밀번호가 일치하지 않음
			msg = "비밀번호가 일치하지 않습니다.";
			model.addAttribute("msg", msg);
			view = "loginForm"; // 비밀번호가 일치하지 않으면 다시 로그인 폼으로 이동
			System.out.println("비번틀림");
		}

		return view;
	}

	public String logout(HttpSession session, RedirectAttributes rttr) {
		log.info("logout()");
		session.invalidate();
		rttr.addFlashAttribute("msg", "로그아웃되었습니다.");
		return "redirect:/";
	}

	public String memberJoin(MemberDto member, RedirectAttributes rttr) {

		log.info("memberJoin()");
		// 가입 성공 시 첫페이지(또는 로그인페이지)로, 실패 시 가입 페이지로 이동
		String view = null;
		String msg = null;

		// 비밀번호 암호화 처리
		String encPwd = pEncoder.encode(member.getPassword());
		log.info(encPwd);
		member.setPassword(encPwd);// 암호화된 비밀번호 다시 저장.

		try {
			mDao.insertMember(member);
			MemberDto adminMember = mDao.selectMember(member.getUsername());
			System.out.println(adminMember);
			if (adminMember.getM_ID() == 1) {
				int m_id = adminMember.getM_ID();
				mDao.updateAdmin(m_id);
				view = "redirect:/";
				msg = "관리자 가입 성공";
			}
			view = "redirect:/";
			msg = "가입 성공";
		} catch (Exception e) {
			e.printStackTrace();
			view = "redirect:joinForm";
			msg = "가입 실패";
		}

		rttr.addFlashAttribute("msg", msg);

		return view;
	}

	public String emailCheck(String username) {
		log.info("emailCheck()");
		String result = null;

		int memCnt = mDao.emailCheck(username);
		if (memCnt == 0) {
			result = "ok";
		} else {
			result = "fail";
		}

		return result;

	}

	public String findById(String m_name, String m_phone, RedirectAttributes rttr) {
		log.info("findById()");
		String view = null;
		String msg = null;
		String idResult = null;

		idResult = mDao.findById(m_name, m_phone);
		System.out.println("mServ" + idResult);

		if (idResult != null) {
			view = "redirect:/loginForm";
			msg = "가입하신 아이디는" + idResult + "입니다.";
		} else {
			view = "redirect:/findEmail";
			msg = "입력하신 정보로 확인되는 아이디가 없습니다. 다시한번 확인해주세요";
		}

		rttr.addFlashAttribute("msg", msg);

		return view;
	}

	public void DriveMemberUpdate(MemberDto member) {
		log.info("DriveMemberUpdate()");

		mDao.updateDriveMemberUpdate(member);
	}

	public String userUpdate(String m_NAME, String m_PHONE, Principal principal, RedirectAttributes rttr,
			HttpSession session) {
		String view;
		String msg;

		// Principal 객체를 Authentication으로 형변환
		Authentication authentication = (Authentication) principal;
		String username = authentication.getName();

		try {
			// 사용자 정보 업데이트
			mDao.UserUpdate(username, m_NAME, m_PHONE);

			// 업데이트된 사용자 정보를 가져와서 UserDetails 객체 생성
			UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(username);

			// 인증 객체 업데이트
			Authentication updatedAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails,
					authentication.getCredentials(), updatedUserDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(updatedAuth);

			// 세션 갱신하지 않음

			msg = "수정 완료";
			view = "redirect:/"; // 로그인 상태를 유지하고 리다이렉트

		} catch (Exception e) {
			e.printStackTrace();
			msg = "수정 실패";
			view = "redirect:UserUpdate";
		}

		rttr.addFlashAttribute("msg", msg);
		return view;
	}

	public void updateRole(int m_id) {
		log.info("updateRole()");

		mDao.updateRole(m_id);
	}

	public void withDrawal(int m_id, RedirectAttributes rttr, HttpSession session) {
		String msg = null;
		maDao.withDrawal(m_id); // 회원 노드 삭제
		aDao.withDrawal(m_id); // 회원 탈퇴 신청 삭제
		bDao.withDrawal(m_id);// 회원 게시글 삭제
		mDao.withDrawal(m_id); // 회원 탈퇴
		session.invalidate(); 
		msg = "회원 탈퇴 완료";
		
		rttr.addFlashAttribute("msg",msg);
	}
	// 비밀번호 변경
	public void pwdChangeProc(RedirectAttributes rttr, String password, String userName) {
		String pass = pEncoder.encode(password);
		mDao.pwdChangeProc(userName,pass);
		
	}
	
}


