package com.kakao.techcampus.wekiki.post;

import com.kakao.techcampus.wekiki.page.PageInfo;
import lombok.Getter;
import lombok.Setter;

public class PostResponse {

    @Getter
    @Setter
    public static class createPostDTO{

        Long postId;
        String title;
        String content;

        public createPostDTO(Post post){
            this.postId = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
        }
    }

}
