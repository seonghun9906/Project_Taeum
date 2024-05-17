package com.icia.Taeumproject.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private static final Map<String, String> ROLE_URL_MAP = new HashMap<>() {{
        put("ROLE_USER", "/");
        put("ROLE_ADMIN", "/adminMain");
        put("ROLE_DRIVER", "/driverModify");
       
    }};

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(ROLE_URL_MAP::containsKey)
                .findFirst()
                .orElse("");

        String redirectUrl = "/";
        String msg = "";

        if (!role.isEmpty() && ROLE_URL_MAP.containsKey(role)) {
            redirectUrl = ROLE_URL_MAP.get(role);
            log.warn("ROLE IS {}", role);
            if (role.equals("ROLE_ADMIN")) {
                log.warn("*****SUPER ADMIN LOGGED IN*****");
            }
        } else {
          msg = "로그인 실패";
        }

        request.getSession().setAttribute(redirectUrl, msg);

        response.sendRedirect(redirectUrl);
    }
    

}


