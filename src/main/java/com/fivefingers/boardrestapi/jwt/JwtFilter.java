package com.fivefingers.boardrestapi.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TYPE_PREFIX = "Bearer ";
    private final JwtAuthenticationProvider provider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(request);
        String requestURL = request.getServletPath();
        if (requestURL.endsWith("reissue")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (StringUtils.hasText(accessToken) && provider.validateToken(accessToken)) {
            Authentication authentication = provider.getAuthenticationToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("jwtFilter : JWT 토큰 인증 완료, URL : " + request.getRequestURL());
        }
            filterChain.doFilter(request,response);
    }

    // Request Header에서 토근 정보 확인
    private String getAccessToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        return (StringUtils.hasText(token) && token.startsWith(TYPE_PREFIX)) ? token.substring(7) : null;

    }
}
