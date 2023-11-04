package com.kakao.techcampus.wekiki.group.domain.member;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.member.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("inactive_group_member")
public class InactiveGroupMember extends GroupMember {
    private LocalDateTime left_at;

    @Builder(builderMethodName = "inactiveGroupMemberBuilder")
    public InactiveGroupMember(Long id, Member member, Group group, String nickName, LocalDateTime created_at, LocalDateTime left_at) {
        super(id, member, group, nickName, created_at);
        this.left_at = left_at;
    }

    /*
        그룹 멤버 탈퇴 전환
     */
    public InactiveGroupMember(ActiveGroupMember groupMember) {
        super(groupMember.getId(), groupMember.getMember(), groupMember.getGroup(), groupMember.getNickName(), groupMember.getCreated_at());
        this.left_at = LocalDateTime.now();
    }
}
