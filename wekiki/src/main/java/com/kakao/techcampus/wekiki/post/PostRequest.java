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

        @Override
        public String toString() {
            return "createPostDTO{" +
                    "pageId=" + pageId +
                    ", parentPostId=" + parentPostId +
                    ", order=" + order +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
