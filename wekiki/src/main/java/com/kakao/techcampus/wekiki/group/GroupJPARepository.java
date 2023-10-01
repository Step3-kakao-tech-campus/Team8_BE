package com.kakao.techcampus.wekiki.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupJPARepository extends JpaRepository<Group, Long> {
}
