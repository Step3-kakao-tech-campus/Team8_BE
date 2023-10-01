package com.kakao.techcampus.wekiki.group.unOfficialGroup;

import lombok.Getter;
import lombok.Setter;

public class UnOfficialGroupRequest {

    @Getter
    @Setter
    public static class CreateClosedGroupDTO {
        private String groupName;
        private String groupImage;
        private String groupNickName;
    }
}
