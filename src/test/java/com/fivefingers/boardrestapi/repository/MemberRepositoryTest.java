package com.fivefingers.boardrestapi.repository;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.domain.member.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static com.fivefingers.boardrestapi.domain.member.Role.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .loginId("mockTest1")
                .password("MockTest123!")
                .username("mockname")
                .build();
        Member member = Member.createMember(createMemberDto, passwordEncoder, ROLE_USER);

        //when
        memberRepository.save(member);

        //then
        assertThat(member.getId()).isEqualTo(1L);
    }


    @DisplayName("회원 Id로 조회")
    @Test
    public void save_findById() throws Exception {
        //given
        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .loginId("mockTest1")
                .password("MockTest123!")
                .username("mockname")
                .build();
        Member member = Member.createMember(createMemberDto, passwordEncoder, ROLE_USER);
        em.persist(member);
        //em.flush();

        //when
        Optional<Member> findMember = memberRepository.findById(1L);

        //then
        assertThat(findMember).isNotEmpty();
        assertThat(findMember.get().getLoginId()).isEqualTo(member.getLoginId());
    }

    @DisplayName("회원 리스트 조회")
    @Test
    public void findAll() throws Exception {
        //given
        CreateMemberDto createMemberA = CreateMemberDto.builder()
                .loginId("mockTestA")
                .password("MockTest123!")
                .username("mocknameA")
                .build();
        CreateMemberDto createMemberB = CreateMemberDto.builder()
                .loginId("mockTestB")
                .password("MockTest123!")
                .username("mocknameB")
                .build();

        Member memberA = Member.createMember(createMemberA, passwordEncoder, ROLE_USER);
        Member memberB = Member.createMember(createMemberB, passwordEncoder, ROLE_USER);
        em.persist(memberA);
        em.persist(memberB);
        em.flush();

        //when
        List<Member> list = memberRepository.findAll();

        //then
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(1).getLoginId()).isEqualTo(memberB.getLoginId());
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
        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .loginId("mockTest1")
                .password("MockTest123!")
                .username("mockname")
                .build();
        Member member = Member.createMember(createMemberDto, passwordEncoder, ROLE_USER);
        em.persist(member);
        em.flush();

        //when
        memberRepository.delete(member);

        //then
        Member findMember = em.find(Member.class, member.getId());
    }


    //멘토
    @DisplayName("회원 삭제 실패")
    @Test
    public void deleteNotValidId() throws Exception {
        //given
        CreateMemberDto createMemberDtoA = CreateMemberDto.builder()
                .loginId("mockTestA")
                .password("MockTest123!")
                .username("mocknameA")
                .build();
        CreateMemberDto createMemberDtoB = CreateMemberDto.builder()
                .loginId("mockTestB")
                .password("MockTest123!")
                .username("mocknameB")
                .build();
        Member memberA = Member.createMember(createMemberDtoA, passwordEncoder, ROLE_USER);
        Member memberB = Member.createMember(createMemberDtoB, passwordEncoder, ROLE_USER);

        em.persist(memberA);
        em.flush();

        //when

        // 예외 발생 x
        em.remove(memberB);

        em.detach(memberA);
        assertThrows(IllegalArgumentException.class, () -> em.remove(memberA));

    }
}