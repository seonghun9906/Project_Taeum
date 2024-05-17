package com.icia.Taeumproject.Dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.icia.Taeumproject.Dto.BoardDto;
import com.icia.Taeumproject.Dto.CommentDto;

@Mapper
public interface BoardDao {
	
	void insertBoard(BoardDto board);
	
	List<BoardDto> getBoardList();

	BoardDto getBoardById(int id);

	// 댓글 가져오기
	List<CommentDto> getComments(int id);
	// 댓글 작성하기
	void insertComment(CommentDto comment);
	// 댓글 삭제
	void cancelComment(int c_ID);
	// 게시글 삭제
	void cacelBoard(int b_ID);
	// 게시글에 대한 모든 댓글 삭제
	void cancelCommentAll(int b_ID);
	// 회원 탈퇴 게시글 삭제
	void withDrawal(int m_id);
	
	
}
