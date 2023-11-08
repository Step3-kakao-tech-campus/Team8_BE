package com.kakao.techcampus.wekiki.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportJPARepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.fromMember.id = :groupMemberId")
    List<Report> findAllByFromMemberId(@Param("groupMemberId") Long groupMemberId);
}
