package com.fivefingers.boardrestapi.jwt;

import com.fivefingers.boardrestapi.domain.member.RefreshToken;
import com.fivefingers.boardrestapi.domain.member.TokenDto;
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

import static com.fivefingers.boardrestapi.jwt.JwtTokenTime.*;
import static java.time.Instant.*;

@Slf4j
@Component
public class JwtAuthenticationProvider {

    private final Key key;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String AUTHORIZATION_TYPE = "bearer";

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

    // 토큰 정보로 클레임을 만들고 이를 이용해 유저 객체를 생성해서 인증이 완료된 UsernamePasswordAuthenticationToken을 반환
    public Authentication getAuthenticationToken(String token) {
        Claims claims = parseClaims(token);
        Collection<? extends GrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
        UserDetails userDetails = User.builder()
                .username(claims.getSubject())
                .password("") // password 안 넣으면 안됨!!!
                .authorities(authorities)
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private List<SimpleGrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    //AuthenticationManager의 authenticate(Authentication authentication)
    public TokenDto createToken(Authentication authentication) {
        Date accessTokenExpireTime = Date.from(
                now().plusSeconds(ACCESS_TOKEN_EXPIRE_Min.getCalculate().apply(30L)));
        Date refreshTokenExpireTime = Date.from(
                now().plusSeconds(REFRESH_TOKEN_EXPIRE_DAY.getCalculate().apply(7L)));

        return TokenDto.builder()
                .grantType(AUTHORIZATION_TYPE)
                .accessToken(createAccessToken(authentication, accessTokenExpireTime))
                .refreshToken(createRefreshToken(refreshTokenExpireTime))
                .accessTokenExpiresIn(accessTokenExpireTime.getTime())
                .build();
    }

    private String createAccessToken(Authentication authentication, Date accessTokenExpireTime) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, getAuthorities(authentication))
                .setExpiration(accessTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createRefreshToken(Date refreshTokenExpireTime) {
        return Jwts.builder()
                .setExpiration(refreshTokenExpireTime)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }


    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
