package com.kakao.techcampus.wekiki.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PageJPARepository extends JpaRepository<PageInfo, Long> {

    @Query("SELECT p FROM PageInfo p WHERE p.pageName LIKE :keyword%")
    Page<PageInfo> findPagesByTitleContainingKeyword(String keyword, Pageable pageable);

//    @Query("SELECT p FROM PageInfo p WHERE p.group.id = :groupId ORDER BY p.updated_at DESC")
//    List<PageInfo> findByGroupIdOrderByUpdatedAtDesc(Long groupId, Pageable pageable);

    @Query("SELECT p FROM PageInfo p ORDER BY p.updated_at DESC")
    List<PageInfo> findOrderByUpdatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM PageInfo p where p.pageName=:title")
    Optional<PageInfo> findByTitle(@Param("title") String title);

}
