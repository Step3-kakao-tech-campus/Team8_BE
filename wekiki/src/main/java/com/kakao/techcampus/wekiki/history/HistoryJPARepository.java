package com.kakao.techcampus.wekiki.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryJPARepository extends JpaRepository<History, Long> {

    @Modifying
    @Query("DELETE FROM History h WHERE h.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Query("SELECT h FROM History h WHERE h.groupMember.id = :groupMemberId")
    List<History> findAllByGroupMemberId(@Param("groupMemberId") Long groupMemberId);

    @Query("SELECT h FROM History h WHERE h.groupMember.id = :groupMemberId")
    Page<History> findAllByGroupMember(@Param("groupMemberId") Long groupMemberId, Pageable pageable);

    @Query("SELECT h FROM History h JOIN FETCH h.groupMember m WHERE h.post.id = :postId ORDER BY h.created_at DESC")
    Page<History> findHistoryWithMemberByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT h FROM History h WHERE h.post.id = :postId ORDER BY h.created_at DESC")
    List<History> findHistoryByPostId(@Param("postId") Long postId, Pageable pageable);
}
