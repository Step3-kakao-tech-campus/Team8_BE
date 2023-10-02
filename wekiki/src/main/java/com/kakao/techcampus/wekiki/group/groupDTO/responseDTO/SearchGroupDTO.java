package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;

import java.util.List;

public record SearchGroupDTO(
        List<OfficialGroup> officialGroups,
        List<UnOfficialOpenedGroup> unofficialOpenedGroups
) {
}

