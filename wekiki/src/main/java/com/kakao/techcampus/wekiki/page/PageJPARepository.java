package com.kakao.techcampus.wekiki.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageJPARepository extends JpaRepository<PageInfo, Long> {

    @Query("SELECT p FROM PageInfo p WHERE p.group.id = :groupId AND p.pageName LIKE :keyword%")
    Page<PageInfo> findPagesByTitleContainingKeyword(@Param("groupId") Long groupId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM PageInfo p WHERE p.group.id = :groupId ORDER BY p.updated_at DESC")
    List<PageInfo> findByGroupIdOrderByUpdatedAtDesc(Long groupId, Pageable pageable);

    @Query("SELECT p FROM PageInfo p where p.group.id = :groupId AND p.pageName=:title")
    Optional<PageInfo> findByTitle(@Param("groupId") Long groupId, @Param("title") String title);

    @Query("SELECT p FROM PageInfo p LEFT JOIN FETCH p.posts ps WHERE p.group.id = :groupId AND p.pageName = :title ORDER BY ps.orders ASC")
    Optional<PageInfo> findByTitleWithPosts(@Param("groupId") Long groupId, @Param("title") String title);

    @Query("SELECT p FROM PageInfo p LEFT JOIN FETCH p.posts ps WHERE p.id = :pageId ORDER BY ps.orders ASC")
    Optional<PageInfo> findByPageIdWithPosts(@Param("pageId") Long pageId);

    @Query("SELECT p FROM PageInfo p LEFT JOIN FETCH p.posts ps WHERE p.group.id = :groupId AND p.pageName LIKE %:keyword%")
    Page<PageInfo> findPagesWithPosts(@Param("groupId") Long groupId, @Param("keyword") String keyword, Pageable pageable);



}
