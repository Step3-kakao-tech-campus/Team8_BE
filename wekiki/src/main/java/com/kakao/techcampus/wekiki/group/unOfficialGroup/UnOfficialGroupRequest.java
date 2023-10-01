package com.kakao.techcampus.wekiki.group.unOfficialGroup;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class UnOfficialGroupRequest {

    @Getter
    @Setter
    public static class CreateClosedGroupDTO {
        @NotNull
        private String groupName;
        @NotNull
        private String groupImage;
        @NotNull
        private String groupNickName;
    }

    @Getter
    @Setter
    public static class CreateOpenedGroupDTO {
        @NotNull
        private String groupName;
        @NotNull
        private String groupImage;
        @NotNull
        private String groupNickName;
        private String introduction;
        @NotNull
        private String entranceHint;
        @NotNull
        private String entrancePassword;
    }
}
