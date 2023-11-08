package com.kakao.techcampus.wekiki.group.repository;

import com.kakao.techcampus.wekiki.group.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberJPARepository extends JpaRepository<GroupMember, Long> {

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.nickName = :nickName")
    Optional<GroupMember> findGroupMemberByNickName(@Param("groupId") Long groupId, @Param("nickName") String nickName);

    // 여기다 멤버 group fetchjoin
    @Query("SELECT gm FROM GroupMember gm LEFT JOIN FETCH gm.member LEFT JOIN FETCH gm.group WHERE gm.member.id = :memberId AND gm.group.id = :groupId")
    Optional<GroupMember> findGroupMemberByMemberIdAndGroupIdFetchJoin(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.member.id = :memberId AND gm.group.id = :groupId")
    GroupMember findGroupMemberByMemberIdAndGroupId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :id")
    List<GroupMember> findAllByGroupId(@Param("id") Long id);
}
