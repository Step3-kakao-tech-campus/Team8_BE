package com.kakao.techcampus.wekiki.report;

import com.kakao.techcampus.wekiki.history.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportJPARepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.history.id = :historyId")
    List<Report> findALLByHistoryId(@Param("historyId") Long historyId);

    @Modifying
    @Query("DELETE FROM Report r WHERE r.history IN :historys")
    void deleteReportsByHistoryInQuery(@Param("historys") List<History> historys);
}
