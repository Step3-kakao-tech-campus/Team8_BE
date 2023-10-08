package com.kakao.techcampus.wekiki.post;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostJPARepository extends JpaRepository<Post, Long> {
    List<Post> findAllByGroupMember(GroupMember groupMember);
}
