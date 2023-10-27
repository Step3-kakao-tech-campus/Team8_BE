package com.kakao.techcampus.wekiki.group.dto.responseDTO;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.OfficialGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialOpenedGroup;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchGroupDTO {
    private List<GroupInfoDTO> officialGroups;
    private List<GroupInfoDTO> unofficialOpenedGroups;

    public SearchGroupDTO(Page<OfficialGroup> officialGroups, Page<UnOfficialOpenedGroup> unofficialOpenedGroups) {
        this.officialGroups = officialGroups.stream().map(GroupInfoDTO::new).collect(Collectors.toList());
        this.unofficialOpenedGroups = unofficialOpenedGroups.stream().map(GroupInfoDTO::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class GroupInfoDTO {
        private String groupName;
        private String groupProfileImage;
        private int memberCount;

        public GroupInfoDTO(Group group) {
            this.groupName = group.getGroupName();
            this.groupProfileImage = group.getGroupProfileImage();
            this.memberCount = group.getMemberCount();
        }
    }
}

