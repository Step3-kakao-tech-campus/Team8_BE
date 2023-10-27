package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateInvitationResponseDTO {
    private Long groupId;

    public ValidateInvitationResponseDTO(Long groupId) {
        this.groupId = groupId;
    }
}
