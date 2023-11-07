package com.kakao.techcampus.wekiki.report;

import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportJPARepository extends JpaRepository<Report, Long> {

    List<Report> findAllByFromMember(ActiveGroupMember activeGroupMember);
}
