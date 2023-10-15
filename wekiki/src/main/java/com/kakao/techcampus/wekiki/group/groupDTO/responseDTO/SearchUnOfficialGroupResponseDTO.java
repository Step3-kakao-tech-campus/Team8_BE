package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchUnOfficialGroupResponseDTO {
    private List<SearchGroupDTO.GroupInfoDTO> unofficialOpenedGroups;

    public SearchUnOfficialGroupResponseDTO(Page<UnOfficialOpenedGroup> unofficialOpenedGroups) {
        this.unofficialOpenedGroups = unofficialOpenedGroups.stream().map(SearchGroupDTO.GroupInfoDTO::new).collect(Collectors.toList());
    }
}
