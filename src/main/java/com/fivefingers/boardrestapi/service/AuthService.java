package com.fivefingers.boardrestapi.service;

import com.fivefingers.boardrestapi.domain.member.RefreshToken;
import com.fivefingers.boardrestapi.domain.member.TokenDto;
import com.fivefingers.boardrestapi.jwt.JwtAuthenticationProvider;
import com.fivefingers.boardrestapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static com.fivefingers.boardrestapi.domain.member.TokenDto.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
        private final RefreshTokenRepository refreshTokenRepository;
        private final JwtAuthenticationProvider jwtAuthenticationProvider;
        private final AuthenticationManagerBuilder authenticationManagerBuilder;

        public TokenDto login(LoginMemberDto loginMemberDto) {
            // 인증 전 토큰 생성
            UsernamePasswordAuthenticationToken beforeAuthenticationToken =
                    loginMemberDto.createBeforeAuthenticationToken();

            // authenticationManagerBuilder -> authenticationManager -> authenticate() -> CustomUserDetailsService
            AuthenticationManager authenticationManager = authenticationManagerBuilder.getObject();
            Authentication afterAuthentication = authenticationManager.authenticate(beforeAuthenticationToken);

            // SecurityContextHolder -> securityContext -> 인증 완료된 afterAuthentication 저장
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(afterAuthentication);

            String accessToken = jwtAuthenticationProvider.createAccessToken(afterAuthentication);
            String refreshToken = saveRefreshToken(jwtAuthenticationProvider.createRefreshToken(afterAuthentication));

            return new TokenDto(accessToken, refreshToken);
        }

        private String saveRefreshToken(RefreshTokenDto refreshTokenDto) {
            // 기존 토큰 조회
            List<RefreshToken> existsToken = findRefreshToken(refreshTokenDto.getLoginId());

            // 있으면 갱신, 없으면 새로 생성
            if (!existsToken.isEmpty()) {
                existsToken.get(0).updateToken(refreshTokenDto);
            } else {
                refreshTokenRepository.save(RefreshToken.from(refreshTokenDto));
            }
            return refreshTokenDto.getValue();
        }

        public TokenDto reissue(RequestTokenDto requestTokenDto) {
            String requestAccessToken = requestTokenDto.getAccessToken();
            String requestRefreshToken = requestTokenDto.getRefreshToken();

            // request AccessToken으로 Authentication 생성
            Authentication authentication = jwtAuthenticationProvider.getAuthenticationToken(requestAccessToken);

            // RefreshToken 체크, 업데이트
            RefreshToken refreshToken = checkedRefreshToken(authentication.getName(), requestRefreshToken);
            refreshToken.updateToken(jwtAuthenticationProvider.createRefreshToken(authentication));
            String accessToken = jwtAuthenticationProvider.createAccessToken(authentication);

            return new TokenDto(accessToken, refreshToken.getValue());
        }

        private RefreshToken checkedRefreshToken(String loginId, String requestRefreshToken) {
            if (!jwtAuthenticationProvider.validateToken(requestRefreshToken)) {
                throw new RuntimeException("유효하지 않은 리프레쉬 토큰");
            }
            List<RefreshToken> findToken = findRefreshToken(loginId);
            if (findToken.isEmpty()) {
                throw new RuntimeException("토큰이 존재하지 않습니다.");
            }
            RefreshToken findRefreshToken = findToken.get(0);
            if (!findRefreshToken.getValue().equals(requestRefreshToken)) {
                throw new RuntimeException("토큰이 일치하지 않습니다.");
            }
            return findRefreshToken;
        }

        @Transactional(readOnly = true)
        public List<RefreshToken> findRefreshToken(String loginId) {
            return refreshTokenRepository.findByLoginId(loginId);
        }
    }
