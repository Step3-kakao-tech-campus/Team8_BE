package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.Group;
import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchGroupDTO {
    private List<GroupInfoDTO> officialGroups;
    private List<GroupInfoDTO> unofficialOpenedGroups;

    public SearchGroupDTO(List<OfficialGroup> officialGroups, List<UnOfficialOpenedGroup> unofficialOpenedGroups) {
        this.officialGroups = GroupInfoDTO.fromGroups(officialGroups);
        this.unofficialOpenedGroups = GroupInfoDTO.fromGroups(unofficialOpenedGroups);
    }

    public static class GroupInfoDTO {
        private String groupName;
        private String groupProfileImage;
        private int memberCount;

        public GroupInfoDTO(String groupName, String groupProfileImage, int memberCount) {
            this.groupName = groupName;
            this.groupProfileImage = groupProfileImage;
            this.memberCount = memberCount;
        }

        public static List<GroupInfoDTO> fromGroups(List<? extends Group> groups) {
            return groups.stream()
                    .map(group -> new GroupInfoDTO(
                            group.getGroupName(),
                            group.getGroupProfileImage(),
                            group.getMemberCount()
                    )).collect(Collectors.toList());
        }
    }
}

