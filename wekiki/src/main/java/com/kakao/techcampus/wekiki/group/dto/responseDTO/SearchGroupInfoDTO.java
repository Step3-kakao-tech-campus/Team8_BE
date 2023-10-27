package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import com.kakao.techcampus.wekiki.group.domain.UnOfficialOpenedGroup;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SearchGroupInfoDTO {
    private String groupName;
    private String groupImage;
    private String introduction;
    private int memberCount;
    private LocalDateTime created_at;
    private String entranceHint;

    public SearchGroupInfoDTO(UnOfficialOpenedGroup group) {
        this.groupName = group.getGroupName();
        this.groupImage = group.getGroupProfileImage();
        this.introduction = group.getIntroduction();
        this.memberCount = group.getMemberCount();
        this.created_at = group.getCreated_at();
        this.entranceHint = group.getEntranceHint();
    }
}

