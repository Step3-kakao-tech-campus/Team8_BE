package com.kakao.techcampus.wekiki.group.unOfficialGroup;

import com.kakao.techcampus.wekiki.group.Group;
import lombok.Getter;
import lombok.Setter;

public class UnOfficialGroupResponse {

    // TODO: Response 수정 필요
    @Getter
    @Setter
    public static class CreateUnOfficialGroupDTO {
        private String groupName;
        private String groupImage;

        public CreateUnOfficialGroupDTO(Group group) {
            this.groupName = group.getGroupName();
            this.groupImage = group.getGroupProfileImage();
        }
    }
}
