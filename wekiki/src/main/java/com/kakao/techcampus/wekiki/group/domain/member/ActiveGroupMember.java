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
@DiscriminatorValue("active_group_member")
public class ActiveGroupMember extends GroupMember {

    @Builder(builderMethodName = "activeGroupMemberBuilder")
    public ActiveGroupMember(Long id, Member member, Group group, String nickName, LocalDateTime created_at) {
        super(id, member, group, nickName, created_at);
    }

    /*
        그룹 멤버 재가입
     */
    public ActiveGroupMember(InactiveGroupMember groupMember) {
        super(groupMember.getId(), groupMember.getMember(), groupMember.getGroup(), groupMember.getNickName(), groupMember.getCreated_at());
    }
}
