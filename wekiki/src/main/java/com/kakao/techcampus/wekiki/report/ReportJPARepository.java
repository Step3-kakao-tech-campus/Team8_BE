package com.kakao.techcampus.wekiki.report;

import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportJPARepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.fromMember.id = :groupMemberId")
    List<Report> findAllByFromMemberId(@Param("groupMemberId") Long groupMemberId);
}
