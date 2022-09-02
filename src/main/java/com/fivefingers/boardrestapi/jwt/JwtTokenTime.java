package com.fivefingers.boardrestapi.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenTime {
    ACCESS_TOKEN_EXPIRE_MIN(60 * 1L), //1분
    REFRESH_TOKEN_EXPIRE_DAY(3600 * 24 * 7L); //7일

    private final Long expired;
}
