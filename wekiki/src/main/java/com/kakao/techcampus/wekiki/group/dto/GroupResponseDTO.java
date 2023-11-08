package com.kakao.techcampus.wekiki.group.dto;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.Invitation;
import com.kakao.techcampus.wekiki.group.domain.OfficialGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.group.domain.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
import jakarta.persistence.DiscriminatorValue;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GroupResponseDTO {

    // 그룹 내 본인 정보 조회
    public record MyGroupInfoResponseDTO(
            Long groupId,
            String groupName,
            String groupNickName,
            List<MyHistoryDTO> historyList
    ) {
        public MyGroupInfoResponseDTO(Group group, GroupMember groupMember, Page<History> histories) {
            this(group.getId(), group.getGroupName(), groupMember.getNickName(), histories.stream().map(MyHistoryDTO::new).collect(Collectors.toList()));
        }

        public record MyHistoryDTO(
                Long historyId,
                String pageName,
                Content content,
                LocalDateTime createdAt
        ) {
            public MyHistoryDTO(History history) {
                this(history.getId(), history.getPost().getPageInfo().getPageName(), new Content(1, history.getTitle(), history.getContent()), history.getCreated_at());
            }
        }

        public record Content(
                double index,
                String name,
                String detail
        ) {
            public Content(double index, String name, String detail) {
                this.index = index;
                this.name = name;
                this.detail = detail;
            }
        }
    }


    // 내 문서 기여 목록 조회
    public record MyGroupHistoryResponseDTO(List<MyGroupInfoResponseDTO.MyHistoryDTO> historyList) {
        public MyGroupHistoryResponseDTO(Page<History> histories) {
            this(histories.stream().map(MyGroupInfoResponseDTO.MyHistoryDTO::new).collect(Collectors.toList()));
        }
    }
    
    // 초대 링크 조회
    public record GetInvitationLinkResponseDTO(Long groupId, String inviteCode) {
        public GetInvitationLinkResponseDTO(Long groupId, Invitation invitation) {
            this(groupId, invitation.code());
        }
    }

    // 초대 링크를 통한 접근
    public record ValidateInvitationResponseDTO(Long groupId, String groupName) {
        public ValidateInvitationResponseDTO(Group group) {
            this(group.getId(), group.getGroupName());
        }
    }

    // 그룹 검색
    public record SearchGroupDTO(
            List<GroupInfoDTO> officialGroups,
            List<GroupInfoDTO> unofficialOpenedGroups
    ) {
        public SearchGroupDTO(Page<OfficialGroup> officialGroups, Page<UnOfficialOpenedGroup> unofficialOpenedGroups) {
            this(officialGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList()),
                    unofficialOpenedGroups.stream().map(GroupInfoDTO::new).collect(Collectors.toList()));
        }

        public record GroupInfoDTO(
                Long groupId,
                String groupName,
                String groupImage
        ) {
            public GroupInfoDTO(Group group) {
                this(group.getId(), group.getGroupName(), group.getGroupProfileImage());
            }
        }
    }

    // 공식 그룹 추가 조회
    public record SearchOfficialGroupResponseDTO(List<SearchGroupDTO.GroupInfoDTO> officialOpenedGroups) {
        public SearchOfficialGroupResponseDTO(Page<OfficialGroup> officialOpenedGroups) {
            this(officialOpenedGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList()));
        }
    }

    // 비공식 공개 그룹 추가 조회
    public record SearchUnOfficialGroupResponseDTO(List<SearchGroupDTO.GroupInfoDTO> unofficialOpenedGroups) {
        public SearchUnOfficialGroupResponseDTO(Page<UnOfficialOpenedGroup> unofficialOpenedGroups) {
            this(unofficialOpenedGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList()));
        }
    }

    // 그룹 정보 상세 조회
    public record SearchGroupInfoDTO(
            Long groupId,
            String groupName,
            String groupImage,
            String introduction,
            int memberCount,
            LocalDateTime created_at,
            String entranceHint,
            String groupType
    ) {
        public SearchGroupInfoDTO(Group group) {
            this(
                    group.getId(),
                    group.getGroupName(),
                    group.getGroupProfileImage(),
                    (group instanceof UnOfficialOpenedGroup) ? ((UnOfficialOpenedGroup) group).getIntroduction() : null,
                    group.getMemberCount(),
                    group.getCreated_at(),
                    (group instanceof UnOfficialOpenedGroup) ? ((UnOfficialOpenedGroup) group).getEntranceHint() : null,
                    group.getClass().getAnnotation(DiscriminatorValue.class).value()
            );
        }
    }


    // 그룹 내 그룹원 리스트 조회
    public record GetGroupMembersResponseDTO(List<String> nickNames) {
        public GetGroupMembersResponseDTO(Group group) {
            this(group.getGroupMembers().stream()
                    .map(GroupMember::getNickName)
                    .collect(Collectors.toList()));
        }
    }

    // 비공식 그룹 생성
    public record CreateUnOfficialGroupResponseDTO(Long groupId, String groupName, String groupImage, String inviteCode) {
        public CreateUnOfficialGroupResponseDTO(Group group, GetInvitationLinkResponseDTO groupInvitationCode) {
            this(group.getId(), group.getGroupName(), group.getGroupProfileImage(), groupInvitationCode.inviteCode);
        }
    }
}
