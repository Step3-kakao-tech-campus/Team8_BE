package com.kakao.techcampus.wekiki.group.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberJPARepository extends JpaRepository<GroupMember, Long> {
}
