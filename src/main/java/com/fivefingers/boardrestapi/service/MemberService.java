package com.fivefingers.boardrestapi.service;

import com.fivefingers.boardrestapi.domain.member.*;
import com.fivefingers.boardrestapi.exception.MemberErrorCode;
import com.fivefingers.boardrestapi.exception.RestApiException;
import com.fivefingers.boardrestapi.jwt.JwtAuthenticationProvider;
import com.fivefingers.boardrestapi.domain.member.TokenDto;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import com.fivefingers.boardrestapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.fivefingers.boardrestapi.domain.member.Authority.*;
import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static com.fivefingers.boardrestapi.domain.member.TokenDto.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public TokenDto login(LoginMemberDto loginMemberDto) {
        // 인증 전 토큰 생성
        UsernamePasswordAuthenticationToken beforeAuthenticationToken =
                loginMemberDto.createBeforeAuthenticationToken();

        AuthenticationManager authenticationManager = authenticationManagerBuilder.getObject();
        // authenticationManager 의 authenticate() 호출 -> CustomUserDetailsService 의 loadUserByUsername 실행
        Authentication authentication = authenticationManager.authenticate(beforeAuthenticationToken);

        // SecurityContextHolder <- securityContext <- authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return createTokenDtoAndSaveToken(authentication);
    }
    @Transactional
    public TokenDto reissue(RequestTokenDto requestTokenDto) {
        if (!jwtAuthenticationProvider.validateToken(requestTokenDto.getRefreshToken())) {
            throw new RuntimeException("Not valid Refresh Token!!");
        }
        Authentication authentication = jwtAuthenticationProvider
                .getAuthenticationToken(requestTokenDto.getAccessToken());

        List<RefreshToken> findRefreshToken = refreshTokenRepository.findByLoginId(authentication.getName());
        if (checkRefreshToken(requestTokenDto, findRefreshToken)) {
            // 확인되면 새로운 토큰을 저장하기위해 기존 토큰 삭제
            log.info("리프레쉬 토큰 인증 완료");
            refreshTokenRepository.delete(findRefreshToken.get(0));
            log.info("기존 토큰 삭제 완료");
        }

        return createTokenDtoAndSaveToken(authentication);
    }

    private TokenDto createTokenDtoAndSaveToken(Authentication authentication) {
        TokenDto tokenDto = jwtAuthenticationProvider.createToken(authentication);

        RefreshToken refreshToken = RefreshToken.createRefreshToken(authentication.getName(), tokenDto.getRefreshToken());
        List<RefreshToken> findRefreshToken = refreshTokenRepository.findByLoginId(authentication.getName());
        if (!findRefreshToken.isEmpty()) {
            refreshTokenRepository.delete(findRefreshToken.get(0));
        }
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    private boolean checkRefreshToken(RequestTokenDto requestTokenDto, List<RefreshToken> findRefreshToken) {
        if (!findRefreshToken.isEmpty()) {
            RefreshToken existsRefreshToken = findRefreshToken.get(0);
            if (!existsRefreshToken.getValue().equals(requestTokenDto.getRefreshToken())) {
                throw new RuntimeException("리프레쉬 토큰 값이 다릅니다.");
            }
        } else {
            throw new RuntimeException("리프레쉬 토큰이 존재하지 않습니다.");
        }
        return true;
    }

    @Transactional
    public Long join(CreateMemberDto createMemberDto) {
        if (!memberRepository.findByLoginId(createMemberDto.getLoginId()).isEmpty())
            throw new RestApiException(MemberErrorCode.DUPLICATE_LOGIN_ID);

        Member member = Member.createMember(createMemberDto, passwordEncoder, createAuthority(Role.ROLE_USER));
        memberRepository.save(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public boolean update(Long memberId, UpdateMemberDto updateMemberDto) {
        Member findMember = findOne(memberId);
        return findMember.updateMember(updateMemberDto);
    }

    @Transactional
    public void delete(Long memberId) {
        Member findMember = findOne(memberId);
        memberRepository.delete(findMember);
    }

}
