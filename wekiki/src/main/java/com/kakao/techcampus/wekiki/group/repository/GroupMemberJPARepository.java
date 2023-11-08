package com.kakao.techcampus.wekiki.group.repository;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.InactiveGroupMember;
import com.kakao.techcampus.wekiki.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberJPARepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT iagm FROM InactiveGroupMember iagm WHERE iagm.member = :member AND iagm.group = :group")
    InactiveGroupMember findInactiveGroupMemberByMemberAndGroup(@Param("member") Member member, @Param("group") Group group);

    @Query("SELECT agm FROM ActiveGroupMember agm WHERE agm.member.id = :memberId AND agm.group.id = :groupId")
    Optional<ActiveGroupMember> findActiveGroupMemberByMemberIdAndGroupId(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.nickName = :nickName")
    Optional<GroupMember> findGroupMemberByNickName(@Param("groupId") Long groupId, @Param("nickName") String nickName);

    // 여기다 멤버 group fetchjoin
    @Query("SELECT agm FROM ActiveGroupMember agm LEFT JOIN FETCH agm.member LEFT JOIN FETCH agm.group WHERE agm.member.id = :memberId AND agm.group.id = :groupId")
    Optional<ActiveGroupMember> findActiveGroupMemberByMemberIdAndGroupIdFetchJoin(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

}
