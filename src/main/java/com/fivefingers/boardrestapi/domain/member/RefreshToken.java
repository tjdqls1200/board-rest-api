package com.fivefingers.boardrestapi.domain.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

import static com.fivefingers.boardrestapi.domain.member.TokenDto.*;
import static com.fivefingers.boardrestapi.jwt.JwtTokenTime.*;
import static java.time.Instant.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private String value;

    private Long refreshTokenExp;

    public static RefreshToken from(RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.loginId = refreshTokenDto.getLoginId();
        refreshToken.value = refreshTokenDto.getValue();
        refreshToken.refreshTokenExp = refreshTokenDto.getRefreshTokenExp();
        return refreshToken;
    }

    public void updateToken(RefreshTokenDto refreshTokenDto) {
        this.value = refreshTokenDto.getValue();
        this.refreshTokenExp = refreshTokenDto.getRefreshTokenExp();
    }
}
