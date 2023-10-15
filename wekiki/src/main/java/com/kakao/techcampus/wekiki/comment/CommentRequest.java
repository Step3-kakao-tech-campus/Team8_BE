package com.kakao.techcampus.wekiki.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CommentRequest {

    @Getter
    public static class createComment{
        private String content;
    }

    @Getter
    public static class updateComment{
        private String content;
    }
}
