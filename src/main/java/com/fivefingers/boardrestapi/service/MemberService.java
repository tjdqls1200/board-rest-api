package com.fivefingers.boardrestapi.service;

import com.fivefingers.boardrestapi.domain.member.*;
import com.fivefingers.boardrestapi.exception.MemberErrorCode;
import com.fivefingers.boardrestapi.exception.RestApiException;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(CreateMemberDto createMemberDto) {
        //회원 조회
        if (!memberRepository.findByLoginId(createMemberDto.getLoginId()).isEmpty())
            throw new RestApiException(MemberErrorCode.DUPLICATE_LOGIN_ID);

        //회원 엔티티 생성
        Member member = Member.createMember(createMemberDto, passwordEncoder, Role.ROLE_USER);

        memberRepository.save(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(() ->
                new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public boolean update(Long memberId, UpdateMemberDto updateMemberDto) {
        Member findMember = findOne(memberId);
        return findMember.updateMember(updateMemberDto, passwordEncoder);
    }

    @Transactional
    public void delete(Long memberId) {
        Member findMember = findOne(memberId);
        memberRepository.delete(findMember);
    }

}
