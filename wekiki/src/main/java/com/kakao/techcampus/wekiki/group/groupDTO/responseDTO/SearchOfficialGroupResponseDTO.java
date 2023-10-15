package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchOfficialGroupResponseDTO {
    private List<SearchGroupDTO.GroupInfoDTO> officialOpenedGroups;

    public SearchOfficialGroupResponseDTO(Page<OfficialGroup> officialOpenedGroups) {
        this.officialOpenedGroups = officialOpenedGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList());
    }
}
