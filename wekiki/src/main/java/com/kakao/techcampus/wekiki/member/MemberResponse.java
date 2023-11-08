package com.kakao.techcampus.wekiki.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public class MemberResponse {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoTokenDTO{
        private String id_token;
        private String token_type;
        private String access_token;
        private int expires_in;
        private String refresh_token;
        private String refresh_token_expires_in;
        private String scope;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KakaoInfoDTO {
        Long id;
        Properties properties;

        @Getter
        public class Properties {
            private String nickname;
        }

    }

    //아직 RefreshToken은 안만들었습니다
    @Getter
    @AllArgsConstructor
    public static class authTokenDTO {
        private String grantType; // Bearer
        private String accessToken;
        @JsonFormat(timezone = "Asia/Seoul")
        private Date accessTokenValidTime;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class myInfoResponseDTO {
        private String mainNickName;
        private List<myInfoGroupDTO> groupList;

        @Getter
        public static class myInfoGroupDTO {
            private Long groupId;
            private String groupImage;
            private String groupName;
            private String groupNickName;

            public myInfoGroupDTO(GroupMember groupMember, Group group) {
                this.groupId = group.getId();
                this.groupImage = group.getGroupProfileImage();
                this.groupName = group.getGroupName();
                this.groupNickName = groupMember.getNickName();
            }
        }
    }
}
