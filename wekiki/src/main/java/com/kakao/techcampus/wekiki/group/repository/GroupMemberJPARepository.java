package com.kakao.techcampus.wekiki.group.repository;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.InactiveGroupMember;
import com.kakao.techcampus.wekiki.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberJPARepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT agm FROM ActiveGroupMember agm WHERE agm.member = :member AND agm.group = :group")
    ActiveGroupMember findActiveGroupMemberByMemberAndGroup(Member member, Group group);
    @Query("SELECT iagm FROM InactiveGroupMember iagm WHERE iagm.member = :member AND iagm.group = :group")
    InactiveGroupMember findInactiveGroupMemberByMemberAndGroup(Member member, Group group);

    @Query("SELECT agm FROM ActiveGroupMember agm WHERE agm.member.id = :memberId AND agm.group.id = :groupId")
    Optional<ActiveGroupMember> findGroupMemberByMemberIdAndGroupId(Long memberId, Long groupId);

    ActiveGroupMember findActiveGroupMemberByMemberIdAndGroupId(Long memberId, Long groupId);
}
