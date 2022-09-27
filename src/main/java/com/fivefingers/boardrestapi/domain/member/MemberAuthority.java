package com.fivefingers.boardrestapi.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_authority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    public static MemberAuthority createMemberAuthority(Member member, Authority authority) {
        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.member = member;
        memberAuthority.authority = authority;
        return memberAuthority;
    }
}
