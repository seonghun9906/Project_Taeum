package com.icia.Taeumproject.Dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.icia.Taeumproject.Dto.ApplyDto;
import com.icia.Taeumproject.Dto.SearchDto;

@Mapper
public interface ApplyDao {

	// 사용자 신청
	void insertApply(ApplyDto apply);

	ApplyDto selectApplyCnt(int a_ID);

	// 중복 신청 체크
	boolean sDuplicateApply(int m_ID,String A_NAME ,String formattedDate);

	// 사용자 신청 내역 가져오기
	List<ApplyDto> getApplyList(@Param("m_id") int m_id, @Param("offset") int offset, @Param("size") int size);

	// 사용자 신청 삭제
	void cancelApply(int applyId);

	List<ApplyDto> selectAllMember();

	int selectAplCnt(SearchDto sdto);
	
	// 회원 탈퇴 신청 삭제
	void withDrawal(int m_id);

}