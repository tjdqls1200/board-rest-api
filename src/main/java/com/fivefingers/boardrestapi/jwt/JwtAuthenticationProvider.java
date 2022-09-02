package com.fivefingers.boardrestapi.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static com.fivefingers.boardrestapi.domain.member.TokenDto.*;
import static com.fivefingers.boardrestapi.jwt.JwtTokenTime.*;
import static java.time.Instant.*;

@Slf4j
@Component
public class JwtAuthenticationProvider {

    private final Key key;
    private static final String AUTHORITIES_KEY = "auth";

    public JwtAuthenticationProvider(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // 토큰 유효 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT 토큰");
        }
        return false;
    }


    public Authentication getAuthenticationToken(String accessToken) {
        // 만료된 AccessToken을 넘겨 받아서 파싱
        Claims claims = parseClaims(accessToken);
        // UserDetails 생성하기 위해 권한 정보 꺼냄
        Collection<? extends GrantedAuthority> authorities = getAuthoritiesFromClaims(claims);

        UserDetails userDetails = User.builder()
                .username(claims.getSubject())
                .password("") // password 안 넣으면 안됨!!!
                .authorities(authorities)
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            return jwtParser.parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // reissue 요청 시 만료된 AccessToken을 보내기 때문에 ExpiredException 발생
            return e.getClaims();
        }
    }


    private Collection<? extends GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        String[] authorities = claims.get(AUTHORITIES_KEY).toString().split(",");
        return Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String createAccessToken(Authentication authentication) {
        Date accessTokenExpireTime = Date.from(
                now().plusSeconds(ACCESS_TOKEN_EXPIRE_MIN.getExpired()));
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, joinAuthorities(authentication))
                .setExpiration(accessTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private String joinAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public RefreshTokenDto createRefreshToken(Authentication authentication) {
        Date refreshTokenExpireTime = Date.from(
                now().plusSeconds(REFRESH_TOKEN_EXPIRE_DAY.getExpired()));
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        return RefreshTokenDto.builder()
                .loginId(authentication.getName())
                .value(refreshToken)
                .refreshTokenExp(refreshTokenExpireTime.getTime())
                .build();
    }

}
