package com.kakao.techcampus.wekiki.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJPARepository extends JpaRepository<Member, Long> {
}
