package com.fivefingers.boardrestapi;

import com.fivefingers.boardrestapi.domain.member.Authority;
import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.domain.member.Role;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import com.fivefingers.boardrestapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


import java.util.ArrayList;
import java.util.List;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

//@Profile("dev")
@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit() {

        }


    }
}
