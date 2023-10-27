package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import com.kakao.techcampus.wekiki.group.domain.Group;

public record CreateUnOfficialGroupResponseDTO(
        String groupName,
        String groupImage
) {
    public CreateUnOfficialGroupResponseDTO(Group group) {
        this(group.getGroupName(), group.getGroupProfileImage());
    }
}
