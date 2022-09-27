package com.fivefingers.boardrestapi.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static com.fivefingers.boardrestapi.domain.member.Role.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(MockitoExtension.class)
class MemberTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Test
    public void createMemberTest() throws Exception {

        //given
        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .loginId("test123")
                .username("testName")
                .password("Password123!")
                .build();
        //when
        Member createdMember = Member.createMember(createMemberDto, passwordEncoder, ROLE_USER);

        //then
        assertThat(createdMember.getLoginId()).isEqualTo("test123");
        assertThat(createdMember.getUsername()).isEqualTo("testName");
        assertThat(passwordEncoder.matches(createMemberDto.getPassword(), createdMember.getPassword())).isTrue();

        assertThat(createdMember.getMemberAuthorities().get(0).getMember()).isEqualTo(createdMember);
        assertThat(createdMember.getMemberAuthorities().get(0).getAuthority().getRole()).isEqualTo(ROLE_USER);
    }

    @Test
    public void updateMemberTest() throws Exception {
        //given
        UpdateMemberDto updateMemberDto = new UpdateMemberDto("Updated123!", "updateUser");
        Member member = Member.builder()
                .loginId("testId123")
                .username("testUser")
                .password(passwordEncoder.encode("Password123!"))
                .build();

        //when
        boolean result1 = member.updateMember(updateMemberDto, passwordEncoder);
        boolean result2 = member.updateMember(updateMemberDto, passwordEncoder);

        //then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }
}