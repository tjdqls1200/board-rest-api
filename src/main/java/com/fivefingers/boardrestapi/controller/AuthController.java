package com.fivefingers.boardrestapi.controller;

import com.fivefingers.boardrestapi.domain.member.MemberDto;
import com.fivefingers.boardrestapi.domain.member.TokenDto;
import com.fivefingers.boardrestapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static com.fivefingers.boardrestapi.domain.member.TokenDto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private static final String AUTHENTICATION_TYPE = "Bearer ";

    @PostMapping("/auth/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginMemberDto loginMemberDto) {
        TokenDto tokenDto = authService.login(loginMemberDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                String.format("%s%s", AUTHENTICATION_TYPE, tokenDto.getAccessToken()));

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(tokenDto);
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody RequestTokenDto requestTokenDto) {
        log.info("reissue controller");
        TokenDto tokenDto = authService.reissue(requestTokenDto);
        return ResponseEntity.ok(tokenDto);
    }

}
