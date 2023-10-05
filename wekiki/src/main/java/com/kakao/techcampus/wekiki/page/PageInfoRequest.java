package com.kakao.techcampus.wekiki.page;

import lombok.Getter;
import lombok.Setter;

public class PageInfoRequest {

    @Getter
    @Setter
    public static class createPageDTO {
        private String title;
        private Long groupId;
    }

    @Getter
    @Setter
    public static class likePageDTO {
        private Long groupId;
    }

    @Getter
    @Setter
    public static class hatePageDTO {
        private Long groupId;
    }

}
