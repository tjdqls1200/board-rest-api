package com.fivefingers.boardrestapi.repository;

import com.fivefingers.boardrestapi.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
// 테스트마다 DB 초기화 @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void init() {
        em.clear();
    }


    @DisplayName("회원 가입")
    @Test
    public void save() throws Exception {
        //given
        Member member = Member.from(createMemberBuild(1));

        //when
        memberRepository.save(member);

        //then
    }

    @DisplayName("회원 아이디로 조회")
    @Test
    public void findById() throws Exception {
        //given
        Member member = Member.from(createMemberBuild(1));
        em.persist(member);

        //when
        Optional<Member> findMember = memberRepository.findById(member.getId());

        //then
        assertThat(findMember.get().getLoginId()).isEqualTo(member.getLoginId());
    }

    @DisplayName("회원 조회 실패시 null 대신 빈 Optional 반환")
    @Test
    public void findByNotValidId() throws Exception {
        //given
        Member member = Member.from(createMemberBuild(1));
        em.persist(member);

        //when
        Optional<Member> optionalMember = memberRepository.findById(100L);

        //then
        assertThat(optionalMember).isEqualTo(Optional.empty());
    }

    @DisplayName("회원 리스트 조회")
    @Test
    public void findAll() throws Exception {
        //given
        Member memberA = Member.from(createMemberBuild(1));
        em.persist(memberA);
        Member memberB = Member.from(createMemberBuild(2));
        em.persist(memberB);

        //when
        // JPQL Query로 인해 flush() 호출
        List<Member> list = memberRepository.findAll();

        //then
        assertThat(list.size()).isEqualTo(2);
    }

    @DisplayName("빈 회원 리스트 조회")
    @Test
    public void findAllEmpty() throws Exception {
        //given

        //when
        List<Member> list = memberRepository.findAll();

        //then
        assertThat(list).isEqualTo(Collections.emptyList());

    }

    @DisplayName("회원 삭제 성공")
    @Test
    public void delete() throws Exception {
        //given
        Member member = Member.from(createMemberBuild(1));
        em.persist(member);
        em.flush();

        //when
        memberRepository.delete(member);

        Member findMember = em.find(Member.class, 1L);
        //then
        assertThat(findMember).isEqualTo(null);
    }


    //멘토
    @DisplayName("회원 삭제 실패")
    @Test
    public void deleteNotValidId() throws Exception {
        //given
        Member memberA = Member.from(createMemberBuild(1));
        Member memberB = Member.from(createMemberBuild(2));
        memberRepository.save(memberA);
        em.flush();
        // 예외 발생 x
        em.remove(memberB);

        em.detach(memberA);
        assertThrows(IllegalArgumentException.class, () -> em.remove(memberA));

    }

    private CreateMemberDto createMemberBuild(int idx) {
        return CreateMemberDto.builder()
                .loginId("mockTest" + idx)
                .password("MockTest123!" + idx)
                .username("mockname" + idx)
                .build();
    }
}