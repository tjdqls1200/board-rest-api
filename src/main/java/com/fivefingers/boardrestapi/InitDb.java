package com.fivefingers.boardrestapi;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.domain.member.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@Profile("dev")
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
            Member test1 = Member.from(new CreateMemberDto("test1234", "Test12341234!", "테스트1"));
            Member test2 = Member.from(new CreateMemberDto("test1235", "Test12351235!", "테스트2"));
            Member test3 = Member.from(new CreateMemberDto("test1236", "Test12361236!", "테스트3"));

            em.persist(test1);
            em.persist(test2);
            em.persist(test3);
        }
    }
}
