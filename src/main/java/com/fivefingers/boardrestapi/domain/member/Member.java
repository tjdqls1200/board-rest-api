package com.fivefingers.boardrestapi.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

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

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 10, nullable = false)
    private String username;

    public static Member from(CreateMemberDto createMemberDto) {
        Member member = new Member();

        member.loginId = createMemberDto.getLoginId();
        member.password = createMemberDto.getPassword();
        member.username = createMemberDto.getUsername();
        return member;
    }

    public boolean updateMember(UpdateMemberDto updateMemberDto) {
        if (updateMemberDto.getPassword().equals(updateMemberDto.getNewPassword()) &&
                this.getUsername().equals(updateMemberDto.getUsername())) {
            return false;
        }
        this.password = updateMemberDto.getNewPassword();
        this.username = updateMemberDto.getUsername();
        return true;
    }
}
