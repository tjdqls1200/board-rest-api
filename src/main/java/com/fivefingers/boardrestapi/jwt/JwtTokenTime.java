package com.fivefingers.boardrestapi.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum JwtTokenTime {
    ACCESS_TOKEN_EXPIRE_Min(min -> 1000 * 60 * min),
    REFRESH_TOKEN_EXPIRE_DAY(day -> (1000 * 60 * 60 * 24) * day);

    //함수형 인터페이스
    private final Function<Long, Long> calculate;
}
