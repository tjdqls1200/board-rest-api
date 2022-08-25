package com.fivefingers.boardrestapi.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;

    @Data
    @AllArgsConstructor
    public static class AccessTokenDto {
        private String accessToken;

        public static AccessTokenDto from(TokenDto tokenDto) {
            return new AccessTokenDto(tokenDto.getAccessToken());
        }
    }

    @Data
    @AllArgsConstructor
    public static class RequestTokenDto {
        private String accessToken;
        private String refreshToken;
    }
}