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
@DiscriminatorValue("active_group_member")
public class ActiveGroupMember extends GroupMember {
    private LocalDateTime joined_at;

    @Builder(builderMethodName = "activeGroupMemberBuilder")
    public ActiveGroupMember(Long id, Member member, Group group, String nickName, LocalDateTime joined_at) {
        super(id, member, group, nickName);
        this.joined_at = joined_at;
    }
}
