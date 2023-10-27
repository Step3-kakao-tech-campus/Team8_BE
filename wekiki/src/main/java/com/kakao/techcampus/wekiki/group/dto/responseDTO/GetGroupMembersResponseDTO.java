package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetGroupMembersResponseDTO {

    private List<GroupMember> groupMemberList;

    public GetGroupMembersResponseDTO(Group group) {
        this.groupMemberList = group.getGroupMembers();
    }
}
