package com.fivefingers.boardrestapi.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenDto {
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String refreshToken;

    @Data
    @AllArgsConstructor
    public static class AccessTokenDto {
        private String accessToken;

        public static AccessTokenDto from(TokenDto tokenDto) {

            return new AccessTokenDto(tokenDto.getAccessToken());
        }
    }

    @Builder
    @Data
    @AllArgsConstructor
    public static class RefreshTokenDto {
        private String value;
        private String loginId;
        private Long refreshTokenExp;
    }

    @Data
    @AllArgsConstructor
    public static class RequestTokenDto {
        private String accessToken;
        private String refreshToken;
    }
}