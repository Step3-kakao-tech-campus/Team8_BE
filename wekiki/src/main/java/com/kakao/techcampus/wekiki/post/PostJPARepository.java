package com.kakao.techcampus.wekiki.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostJPARepository extends JpaRepository<Post, Long> {
    List<Post> findAllByGroupMember(Long id);
}
