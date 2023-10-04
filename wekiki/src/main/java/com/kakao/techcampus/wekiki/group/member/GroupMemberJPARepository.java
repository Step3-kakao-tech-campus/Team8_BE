package com.kakao.techcampus.wekiki.group.member;

import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberJPARepository extends JpaRepository<GroupMember, Long> {
    GroupMember findByMemberAndGroup(Member member, UnOfficialOpenedGroup group);
}
