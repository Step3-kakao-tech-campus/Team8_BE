package com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup;

import com.kakao.techcampus.wekiki.group.Group;
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
@DiscriminatorValue("un_official_closed_group")
public class UnOfficialClosedGroup extends Group {

    @Builder(builderMethodName = "unOfficialClosedGroupBuilder")
    public UnOfficialClosedGroup(Long id, String groupName, String groupProfileImage, int memberCount, LocalDateTime created_at) {
        super(id, groupName, groupProfileImage, memberCount, created_at);
    }
}
