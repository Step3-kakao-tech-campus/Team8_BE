package com.kakao.techcampus.wekiki.group.member;

import com.kakao.techcampus.wekiki.group.Group;
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
    public InactiveGroupMember(Long id, Member member, Group group, String nickName, LocalDateTime left_at) {
        super(id, member, group, nickName);
        this.left_at = left_at;
    }
}
