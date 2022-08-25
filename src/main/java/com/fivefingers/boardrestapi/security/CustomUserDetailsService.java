package com.fivefingers.boardrestapi.security;

import com.fivefingers.boardrestapi.domain.member.Authority;
import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.domain.member.MemberAuthority;
import com.fivefingers.boardrestapi.domain.member.Role;
import com.fivefingers.boardrestapi.exception.MemberErrorCode;
import com.fivefingers.boardrestapi.exception.RestApiException;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Member> findMember = memberRepository.findByLoginId(username);
        if (findMember.isEmpty()) {
            throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        Member member = findMember.get(0);
        Collection<SimpleGrantedAuthority> authorities = getAuthorities(member.getMemberAuthorities());
        return new User(member.getUsername(), member.getPassword(), authorities);
    }

    private Collection<SimpleGrantedAuthority> getAuthorities(List<MemberAuthority> memberAuthorities) {
        return memberAuthorities.stream()
                .map(MemberAuthority::getAuthority)
                .map(Authority::getRole)
                .map(Enum::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
