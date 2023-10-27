package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import com.kakao.techcampus.wekiki.group.domain.Invitation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInvitationLinkResponseDTO {
    private String invitationLink;

    public GetInvitationLinkResponseDTO(Invitation invitation) {
        this.invitationLink = invitation.code();
    }
}
