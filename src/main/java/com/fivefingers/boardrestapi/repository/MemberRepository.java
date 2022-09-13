package com.fivefingers.boardrestapi.repository;

import com.fivefingers.boardrestapi.domain.member.Authority;
import com.fivefingers.boardrestapi.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public List<Member> findByLoginId(String loginId) {
        return em.createQuery("select m from Member m where m.loginId =: loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public Optional<Member> findById(Long memberId) {
        return Optional.ofNullable(em.find(Member.class, memberId));
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public void delete(Member member) {
        em.remove(member);
    }
}
