package com.kakao.techcampus.wekiki.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostJPARepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.pageInfo.id = :pageId AND p.orders >= :orders")
    List<Post> findPostsByPageIdAndOrderGreaterThan(
            @Param("pageId") Long pageId,
            @Param("orders") int orders
    );

    @Query("SELECT p FROM Post p WHERE p.pageInfo.id = :pageId ORDER BY p.orders ASC")
    List<Post> findPostsByPageIdOrderByOrderAsc(@Param("pageId") Long pageId);

    boolean existsByPageInfoId(Long pageInfoId);

    boolean existsByParentId(Long parentId);

    @Query("SELECT p FROM Post p WHERE p.groupMember.id = :groupId")
    List<Post> findAllByGroupMember(@Param("groupId") Long groupId);

    // 해당 pageId를 가지고 있는 post들 중에 orders가 1인거 들고오기
    @Query("SELECT p FROM Post p WHERE p.pageInfo.id = :pageId AND p.orders = 1")
    List<Post> findFirstPost(@Param("pageId") Long pageId);
}
