package com.fivefingers.boardrestapi.domain.member;

import com.fivefingers.boardrestapi.jwt.JwtTokenTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

import static com.fivefingers.boardrestapi.jwt.JwtTokenTime.*;
import static java.time.Instant.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String value;

    private Long refreshTokenExp;

    public static RefreshToken createRefreshToken(String loginId, String refreshTokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.loginId = loginId;
        refreshToken.value = refreshTokenValue;
        return refreshToken;
    }

    public void renewalExp() {
        this.refreshTokenExp = Date.from(
                now().plusSeconds(REFRESH_TOKEN_EXPIRE_DAY.getCalculate().apply(7L))).getTime();
    }
}
