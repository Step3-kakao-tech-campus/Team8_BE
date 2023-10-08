package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.Group;

public record CreateUnOfficialGroupResponseDTO(
        String groupName,
        String groupImage
) {
    public CreateUnOfficialGroupResponseDTO(Group group) {
        this(group.getGroupName(), group.getGroupProfileImage());
    }
}
