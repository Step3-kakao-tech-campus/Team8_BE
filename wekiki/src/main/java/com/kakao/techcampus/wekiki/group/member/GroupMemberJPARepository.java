package com.kakao.techcampus.wekiki.group.member;

import com.kakao.techcampus.wekiki.group.Group;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberJPARepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT agm FROM ActiveGroupMember agm WHERE agm.member = :member AND agm.group = :group")
    ActiveGroupMember findActiveGroupMemberByMemberAndGroup(Member member, UnOfficialOpenedGroup group);
    @Query("SELECT iagm FROM InactiveGroupMember iagm WHERE iagm.member = :member AND iagm.group = :group")
    InactiveGroupMember findInactiveGroupMemberByMemberAndGroup(Member member, UnOfficialOpenedGroup group);

    @Query("SELECT agm FROM ActiveGroupMember agm WHERE agm.member.id = :memberId AND agm.group.id = :groupId")
    Optional<ActiveGroupMember> findGroupMemberByMemberIdAndGroupId(Long memberId, Long groupId);
}
