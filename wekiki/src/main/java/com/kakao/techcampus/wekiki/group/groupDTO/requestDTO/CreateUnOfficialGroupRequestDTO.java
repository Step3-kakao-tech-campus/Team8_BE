package com.kakao.techcampus.wekiki.group.groupDTO.requestDTO;

import com.kakao.techcampus.wekiki.group.GroupType;
import jakarta.validation.constraints.NotNull;

public record CreateUnOfficialGroupRequestDTO(
        @NotNull GroupType groupType,
        @NotNull String groupName,
        @NotNull String groupImage,
        @NotNull String groupNickName,
        String introduction,
        String entranceHint,
        String entrancePassword
) {
}
