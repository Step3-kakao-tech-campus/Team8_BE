package com.kakao.techcampus.wekiki.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MemberJPARepository extends JpaRepository<Member, UUID> {
    @Query("select m from Member m where m.email=:email")
    Optional<Member> findByEmail(@Param("email") String email);

}
