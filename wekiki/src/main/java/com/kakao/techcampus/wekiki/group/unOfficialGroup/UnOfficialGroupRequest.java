package com.kakao.techcampus.wekiki.group.unOfficialGroup;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class UnOfficialGroupRequest {

    @Getter
    @Setter
    public static class CreateUnOfficialGroupDTO {
        // TODO: groupType 타입은 뭘로 할지 결정
        @NotNull
        private int groupType;
        @NotNull
        private String groupName;
        @NotNull
        private String groupImage;
        @NotNull
        private String groupNickName;
        private String introduction;
        private String entranceHint;
        private String entrancePassword;
    }
}
