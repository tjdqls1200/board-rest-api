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

    // 토큰의 인증 정보를 SecurityContext에 저장
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);
        log.info("인증 jwt = " + jwt);
        // jwt Token 확인 후 UsernamePasswordAuthenticationToken을 생성해서 SecurityContext에 저장
        if (StringUtils.hasText(jwt) && provider.validateToken(jwt)) {
            Authentication authentication = provider.getAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("jwtFilter : JWT 토큰 인증 완료, URL : " + request.getRequestURL() );
        } else {
            // AccessToken이 만료되면 
            log.info("jwtFilter : 유효한 JWT 토큰 없음, URL : " + request.getRequestURL() );
        }
        filterChain.doFilter(request, response);
    }

    // Request Header에서 토근 정보 확인
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        return (StringUtils.hasText(token) && token.startsWith(TYPE_PREFIX)) ? token.substring(7) : null;

    }
}
