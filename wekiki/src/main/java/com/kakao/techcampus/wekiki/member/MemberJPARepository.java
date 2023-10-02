package com.kakao.techcampus.wekiki.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberJPARepository extends JpaRepository<Member, UUID> {
}
