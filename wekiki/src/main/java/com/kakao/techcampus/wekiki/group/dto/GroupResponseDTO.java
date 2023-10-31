package com.kakao.techcampus.wekiki.group.dto;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.Invitation;
import com.kakao.techcampus.wekiki.group.domain.OfficialGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroupResponseDTO {
    public record GetInvitationLinkResponseDTO(String invitationLink) {
        public GetInvitationLinkResponseDTO(Invitation invitation) {
            this(invitation.code());
        }
    }

    public record MyGroupInfoResponseDTO(
            String groupName,
            String groupNickName,
            List<MyHistoryDTO> myHistorgiyDTOS
    ) {
        public MyGroupInfoResponseDTO(Group group, GroupMember groupMember, Page<History> histories) {
            this(group.getGroupName(), groupMember.getNickName(), histories.stream().map(MyHistoryDTO::new).collect(Collectors.toList()));
        }

        public record MyHistoryDTO(
                String content,
                LocalDateTime created_at
        ) {
            public MyHistoryDTO(History history) {
                this(history.getContent(), history.getCreated_at());
            }
        }
    }

    public record SearchUnOfficialGroupResponseDTO(List<SearchGroupDTO.GroupInfoDTO> unofficialOpenedGroups) {
        public SearchUnOfficialGroupResponseDTO(Page<UnOfficialOpenedGroup> unofficialOpenedGroups) {
            this(unofficialOpenedGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList()));
        }
    }

    public record MyGroupHistoryResponseDTO(List<MyGroupInfoResponseDTO.MyHistoryDTO> myHistoryDTOS) {
        public MyGroupHistoryResponseDTO(Page<History> histories) {
            this(histories.stream().map(MyGroupInfoResponseDTO.MyHistoryDTO::new).collect(Collectors.toList()));
        }
    }

    public record ValidateInvitationResponseDTO(Long groupId) {
    }


    public record SearchGroupInfoDTO(
            String groupName,
            String groupImage,
            String introduction,
            int memberCount,
            LocalDateTime created_at,
            String entranceHint
    ) {
        public SearchGroupInfoDTO(UnOfficialOpenedGroup group) {
            this(group.getGroupName(), group.getGroupProfileImage(), group.getIntroduction(), group.getMemberCount(), group.getCreated_at(), group.getEntranceHint());
        }
    }

    public record SearchOfficialGroupResponseDTO(List<SearchGroupDTO.GroupInfoDTO> officialOpenedGroups) {
        public SearchOfficialGroupResponseDTO(Page<OfficialGroup> officialOpenedGroups) {
            this(officialOpenedGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList()));
        }
    }

    public record GetGroupMembersResponseDTO(List<ActiveGroupMember> activeGroupMemberList) {
        public GetGroupMembersResponseDTO(Group group) {
            this(group.getGroupMembers().stream()
                    .filter(groupMember -> groupMember instanceof ActiveGroupMember)
                    .map(groupMember -> (ActiveGroupMember) groupMember)
                    .collect(Collectors.toList()));
        }
    }

    public record CreateUnOfficialGroupResponseDTO(String groupName, String groupImage) {
        public CreateUnOfficialGroupResponseDTO(Group group) {
            this(group.getGroupName(), group.getGroupProfileImage());
        }
    }

    public record SearchGroupDTO(
            List<GroupInfoDTO> officialGroups,
            List<GroupInfoDTO> unofficialOpenedGroups
    ) {
        public SearchGroupDTO(Page<OfficialGroup> officialGroups, Page<UnOfficialOpenedGroup> unofficialOpenedGroups) {
            this(officialGroups.stream().map(GroupInfoDTO::new).collect(Collectors.toList()), unofficialOpenedGroups.stream().map(GroupInfoDTO::new).collect(Collectors.toList()));
        }

        public record GroupInfoDTO(
                String groupName,
                String groupProfileImage,
                int memberCount
        ) {
            public GroupInfoDTO(Group group) {
                this(group.getGroupName(), group.getGroupProfileImage(), group.getMemberCount());
            }
        }
    }
}
