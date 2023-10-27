package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class GetGroupMembersResponseDTO {

    private List<ActiveGroupMember> activeGroupMemberList;

    public GetGroupMembersResponseDTO(Group group) {
        this.activeGroupMemberList = group.getGroupMembers().stream()
                .filter(groupMember -> groupMember instanceof ActiveGroupMember)
                .map(groupMember -> (ActiveGroupMember) groupMember)
                .collect(Collectors.toList());
    }
}
