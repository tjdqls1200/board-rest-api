package com.fivefingers.boardrestapi.service;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.exception.MemberErrorCode;
import com.fivefingers.boardrestapi.exception.RestApiException;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import java.util.List;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;


    @Transactional
    public Long join(Member member) {
        if (!memberRepository.findByLoginId(member.getLoginId()).isEmpty())
            throw new RestApiException(MemberErrorCode.DUPLICATE_LOGIN_ID);
        memberRepository.save(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public boolean update(Long memberId, UpdateMemberDto updateMemberDto) {
        Member findMember = findOne(memberId);
        return findMember.updateMember(updateMemberDto);
    }

    @Transactional
    public void delete(Long memberId) {
        Member findMember = findOne(memberId);
        memberRepository.delete(findMember);
    }

}
