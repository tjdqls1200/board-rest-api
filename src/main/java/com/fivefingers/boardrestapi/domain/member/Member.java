package com.fivefingers.boardrestapi.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @JsonIgnore
    private boolean enabled;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAuthority> memberAuthorities = new ArrayList<>();


    public static Member from(CreateMemberDto createMemberDto) {

        Member member = new Member();

        member.loginId = createMemberDto.getLoginId();
        member.password = createMemberDto.getPassword();
        member.username = createMemberDto.getUsername();

        return member;
    }

    public void addMemberAuthority(MemberAuthority memberAuthority) {
        memberAuthorities.add(memberAuthority);
        memberAuthority.addMember(this);
    }

    public static Member createMember(
            CreateMemberDto createMemberDto,
            PasswordEncoder passwordEncoder,
            Authority... authorities) {
        Member member = new Member();
        member.loginId = createMemberDto.getLoginId();
        member.password = passwordEncoder.encode(createMemberDto.getPassword());
        member.username = createMemberDto.getUsername();
        member.enabled = true;
        List<MemberAuthority> memberAuthorities = Arrays.stream(authorities)
                .map(MemberAuthority::addAuthroity)
                .collect(Collectors.toList());
        for (MemberAuthority memberAuthority : memberAuthorities) {
            member.addMemberAuthority(memberAuthority);
        }
        return member;
    }


    public void passwordEncoding(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }


    public boolean updateMember(UpdateMemberDto updateMemberDto) {
        if (password.equals(updateMemberDto.getNewPassword()) &&
                username.equals(updateMemberDto.getUsername())) {
            return false;
        }
        this.password = updateMemberDto.getNewPassword();
        this.username = updateMemberDto.getUsername();
        return true;
    }
}
