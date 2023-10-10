package com.kakao.techcampus.wekiki.post;

import com.kakao.techcampus.wekiki.page.PageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kakao.techcampus.wekiki.group.member.GroupMember;

import java.util.List;

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

    List<Post> findAllByGroupMember(GroupMember groupMember);
}
