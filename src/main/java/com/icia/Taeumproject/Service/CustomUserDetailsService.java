package com.icia.Taeumproject.Service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.icia.Taeumproject.Dao.MemberDao;

import com.icia.Taeumproject.Dto.MemberDto;
import com.icia.Taeumproject.Dto.SecurityUserDTO;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDto member = Optional.ofNullable(memberDao.findUserByEmail(username))
                                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SecurityUserDTO(member);
    }

    public List<MemberDto> loadUsersByRole(String roleName) {
      // 역할에 따른 사용자 목록을 저장할 리스트
      List<MemberDto> usersByRole = new ArrayList<>();

      // 사용자 테이블과 역할 테이블을 조인하여 역할이 roleName인 사용자들을 가져오는 쿼리 실행
      List<MemberDto> allUsers = memberDao.getAllUsers(); // 예시로 모든 사용자를 가져오는 메서드 사용

      // 각 사용자의 역할을 확인하고 roleName과 일치하는 경우에만 결과 리스트에 추가
      for (MemberDto user : allUsers) {	
          if (user.getRole().equals(roleName)) {
              usersByRole.add(user);
          }
      }

      return usersByRole;
  }
}
