package com.kakao.techcampus.wekiki.history;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryJPARepository extends JpaRepository<History, Long> {
    List<History> findByGroupMember(GroupMember groupMember);
}
