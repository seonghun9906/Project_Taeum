package com.icia.Taeumproject.Dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.Node;

@Mapper
public interface MemberDao {

	// 회원가입 메서드
	void insertMember(MemberDto member);

	// 로그인 비밀번호를 가져오는 메서드
	String selectPassword(String username);

	// 로그인 후 회원 정보를 가져오는 메서드
	MemberDto selectMember(String username);

	// 이메일 중복 체크 메서드
	int emailCheck(String memail);

	// 메일주소를 가져오는 메서드
	String selectEmail(String M_NAME);

	MemberDto findUserByEmail(String username);

	String findById(String m_name, String m_phone);

	void updateRole(int mid);

	void updateDriveMemberUpdate(MemberDto member);

	void UserUpdate(String username, String m_NAME, String m_PHONE);

	List<MemberDto> getAllUsers();

	
	void updateAdmin(int m_id);
	// 회원 탈퇴
	void withDrawal(int m_id);
    // 비밀번호 변경
	void pwdChangeProc(String userName, String pass);

}