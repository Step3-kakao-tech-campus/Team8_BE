package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class SearchGroupInfoDTO {
    String groupName;
    String groupImage;
    String introduction;
    int memberCount;
    LocalDateTime created_at;
    String entranceHint;
    String entrancePassword;

    public SearchGroupInfoDTO(UnOfficialOpenedGroup group) {
        this.groupName = group.getGroupName();
        this.groupImage = group.getGroupProfileImage();
        this.introduction = group.getIntroduction();
        this.memberCount = group.getMemberCount();
        this.created_at = group.getCreated_at();
        this.entranceHint = group.getEntranceHint();
        this.entrancePassword = group.getEntrancePassword();
    }
}

