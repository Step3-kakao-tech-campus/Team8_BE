package com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup;

import com.kakao.techcampus.wekiki.group.Group;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.kakao.techcampus.wekiki.group.invitation.Invitation;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("un_official_closed_group")
public class UnOfficialClosedGroup extends Group {

    @OneToOne
    private Invitation invitation;

    @Builder(builderMethodName = "unOfficialClosedGroupBuilder")
    public UnOfficialClosedGroup(Long id, String groupName, String groupProfileImage, LocalDateTime created_at) {
        super(id, groupName, groupProfileImage, created_at);
        this.invitation = Invitation.builder().group(this).build();
    }
}
