package com.fivefingers.boardrestapi.service;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.exception.MemberErrorCode;
import com.fivefingers.boardrestapi.exception.RestApiException;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public void join(Member member) {
        if (!memberRepository.findByLoginId(member.getLoginId()).isEmpty())
            // loginId가 unique index이면 굳이 아이디를 체크하지 않아도 DB Exception이 뜰거 같은데
            throw new RestApiException(MemberErrorCode.DUPLICATE_LOGIN_ID);
        memberRepository.save(member);
    }
    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public boolean update(Long memberId, UpdateMemberDto updateMemberDto) {
        Member findMember = findOne(memberId);
        return findMember.updateMember(updateMemberDto);
    }

    public void delete(Long memberId) {
        memberRepository.delete(memberId);
    }

}
