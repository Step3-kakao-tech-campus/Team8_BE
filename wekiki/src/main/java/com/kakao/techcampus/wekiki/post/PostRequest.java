package com.kakao.techcampus.wekiki.post;

import lombok.Getter;
import lombok.Setter;

public class PostRequest {
    @Getter
    @Setter
    public static class createPostDTO {
        private Long pageId;
        private Long parentPostId;
        private int order;
        private String title;
        private String content;

    }

    @Getter
    @Setter
    public static class modifyPostDTO{
        private Long postId;
        private String title;
        private String content;
    }

    @Getter
    @Setter
    public static class createReportDTO{
        private Long postId;
        private String content;
    }
}
