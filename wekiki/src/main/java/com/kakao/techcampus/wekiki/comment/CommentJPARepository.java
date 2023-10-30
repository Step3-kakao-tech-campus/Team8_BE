package com.kakao.techcampus.wekiki.comment;

import com.kakao.techcampus.wekiki.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentJPARepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.groupMember WHERE c.post.id = :postId ORDER BY c.created_at")
    Page<Comment> findCommentsByPostIdWithGroupMembers(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.groupMember WHERE c.id = :commentId")
    Optional<Comment> findCommentWithGroupMember(@Param("commentId") Long commentId);



}
