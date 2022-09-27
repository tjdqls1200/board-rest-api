package com.fivefingers.boardrestapi.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;


import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String loginId;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(length = 10, nullable = false)
    private String username;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAuthority> memberAuthorities = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String username, List<MemberAuthority> memberAuthorities) {
        Assert.hasText(loginId, "must not be empty!");
        Assert.hasText(password, "must not be empty");
        Assert.hasText(username, "must not be empty");

        this.loginId = loginId;
        this.password = password;
        this.username = username;
    }

    public static Member createMember(
            CreateMemberDto createMemberDto,
            PasswordEncoder passwordEncoder,
            Role... roles) {
        // 회원 Entity 생성
        Member member = Member.builder()
                .loginId(createMemberDto.getLoginId())
                .password(passwordEncoder.encode(createMemberDto.getPassword()))
                .username(createMemberDto.getUsername())
                .build();
        for (Role role : roles) {
            Authority authority = Authority.createAuthorities(role);
            MemberAuthority memberAuthority = MemberAuthority.createMemberAuthority(member, authority);
            member.memberAuthorities.add(memberAuthority);
        }
        return member;
    }
    public boolean updateMember(UpdateMemberDto updateMemberDto, PasswordEncoder passwordEncoder) {
        boolean updated = false;
        String newPassword = updateMemberDto.getNewPassword();
        String newUsername = updateMemberDto.getUsername();

        if (!passwordEncoder.matches(newPassword, this.password)) {
            password = passwordEncoder.encode(newPassword);
            updated = true;
        }
        if (!(username.equals(newUsername))) {
            username = newUsername;
            updated = true;
        }
        return updated;
    }
}
