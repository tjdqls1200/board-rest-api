package com.fivefingers.boardrestapi.repository;

import com.fivefingers.boardrestapi.domain.member.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final EntityManager em;

    public void save(RefreshToken refreshToken) {
        em.persist(refreshToken);
    }

    public List<RefreshToken> findByLoginId(String loginId) {
        return em.createQuery(
                "select r from RefreshToken r where r.loginId =: loginId", RefreshToken.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public void delete(RefreshToken refreshToken) {
        em.remove(refreshToken);
    }
}
