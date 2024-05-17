package com.icia.Taeumproject.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icia.Taeumproject.Dto.BoardDto;
import com.icia.Taeumproject.Dto.CommentDto;
import com.icia.Taeumproject.Dto.SecurityUserDTO;
import com.icia.Taeumproject.Service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j

public class BoardController {
	 @Autowired
	 private BoardService bServ;
	
	@GetMapping("board")
	public String board(Model model) {
		log.info("board");
		
		bServ.boardList(model);
		return "board";
	}
	
	
	@GetMapping("boardWrite")
	public String boardWrite() {
		log.info("boardWrite");
		
		return "boardWrite";
	}
	
	// 게시글 작성하기 컨트롤러
	@PostMapping("bWriteProc")
	public String bWriteProc(BoardDto board, RedirectAttributes rttr) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    Object principal = authentication.getPrincipal();
	    String username = ((SecurityUserDTO)principal).getM_NAME();
	    
	    bServ.bWriteProc(board, rttr, username); // username = M_NAME
	    
	    // 게시글 작성 후 게시글 목록 페이지로 리다이렉트
	    return "redirect:/board";
	}
	   @GetMapping("/boardDetail/{id}")
	    public String getBoardDetail(@PathVariable("id") int id, Model model) {
		   // 게시글 데이터 가져오기
	        BoardDto boardDto = bServ.getBoardById(id);
	        // 댓글 데이터 가져오기
	        List<CommentDto> comments = bServ.getCommentsByBoardId(id);
	        model.addAttribute("boardDto", boardDto);
	        model.addAttribute("comments", comments);
	        return "boardDetail"; // 게시글 상세 페이지의 HTML 파일명
	    }
	   
	    @PostMapping("commentProc")
	    public String commentProc(CommentDto comment,Model model) {
	    	
	    	bServ.CommentProc(comment,model);
	    	// 리다이렉트할 때 게시글의 ID를 함께 전달
	        int boardId = comment.getB_ID();
	        return "redirect:/boardDetail/" + boardId;
	    }
	   
	    @PostMapping("/cancelComment")
	    public String cancelComment(int C_ID,int B_ID,RedirectAttributes rttr) {
	    	
	    	log.info("cancelCommentcancelCommentcancelC");
	    	bServ.cancelComment(C_ID,rttr);
	    	 return "redirect:/boardDetail/" + B_ID;
	    }
	    
	    @PostMapping("/cancelBoard")
	    public String cancelBoard(int B_ID,RedirectAttributes rttr) {
	    	bServ.cancelCommentAll(B_ID, rttr);
	    	bServ.cancelBoard(B_ID, rttr);
	    	return "redirect:/board";
	    	
	    }
	    
	
	    }















