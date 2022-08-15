package com.fivefingers.boardrestapi.service;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.exception.DuplicateMemberException;
import com.fivefingers.boardrestapi.exception.LoginNotEqualsException;
import com.fivefingers.boardrestapi.exception.MemberNotFoundException;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public void join(Member member) {
        if (!memberRepository.findByLoginId(member.getLoginId()).isEmpty())
            throw new DuplicateMemberException(String.format("[%s]는 이미 존재하는 아이디입니다.", member.getLoginId()));
        memberRepository.save(member);
    }

    public Member find(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("없는 회원"));
    }

    public boolean update(UpdateMemberDto updateMemberDto) {
        Member findMember = find(updateMemberDto.getId());
        loginChecked(findMember, updateMemberDto.getLoginId(), updateMemberDto.getPassword());
        return findMember.updateMember(updateMemberDto);
    }

    public void delete(DeleteMemberDto deleteMemberDto) {
        Member findMember = find(deleteMemberDto.getId());
        loginChecked(findMember, deleteMemberDto.getLoginId(), deleteMemberDto.getPassword());
        memberRepository.delete(findMember);
    }

    private void loginChecked(Member member, String loginId , String password) {
        if (!member.getLoginId().equals(loginId)) {
            throw new LoginNotEqualsException("아이디가 일치하지 않습니다.");
        }
        if (!member.getPassword().equals(password)) {
            throw new LoginNotEqualsException("패스워드가 일치하지 않습니다.");
        }
    }
}
