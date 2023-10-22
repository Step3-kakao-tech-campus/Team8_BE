package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.invitation.Invitation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInvitationLinkResponseDTO {
    private String invitationLink;

    public GetInvitationLinkResponseDTO(Invitation invitation) {
        this.invitationLink = invitation.getInvitationLink();
    }
}
