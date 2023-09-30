package com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup;

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
@DiscriminatorValue("un_official_opened_group")
public class UnOfficialOpenedGroup extends Group {

    private String introduction;
    private String entranceHint;
    private String entrancePassword;

    @Builder(builderMethodName = "unOfficialOpenedGroupBuilder")
    public UnOfficialOpenedGroup(Long id, String groupName, String groupProfileImage, int memberCount, LocalDateTime created_at, String introduction, String entranceHint, String entrancePassword) {
        super(id, groupName, groupProfileImage, memberCount, created_at);
        this.introduction = introduction;
        this.entranceHint = entranceHint;
        this.entrancePassword = entrancePassword;
    }
}
