package com.kakao.techcampus.wekiki.comment;

import com.kakao.techcampus.wekiki.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Getter
    @Setter
    public static class createCommentDTO{

        Long commentId;
        String content;
        LocalDateTime createdAt;
        String nickName;

        public createCommentDTO(Comment comment, String nickName){
            this.commentId = comment.getId();
            this.content = comment.getContent();
            this.createdAt = comment.getCreated_at();
            this.nickName = nickName;
        }
    }

    @Getter
    @Setter
    public static class getCommentDTO{

        Long postId;
        List<commentDTO> comments;

        public getCommentDTO(Post post, List<commentDTO> comments){
            this.postId = post.getId();
            this.comments = comments;
        }

        @Getter @Setter
        public static class commentDTO{
            Long commentId;
            //String nickName;
            String content;
            LocalDateTime createdAt;

            public commentDTO(Comment comment){
                this.commentId = comment.getId();
                //this.nickName = "temp";
                this.content = comment.getContent();
                this.createdAt = comment.getCreated_at();
            }
        }

    }

    @Getter
    @Setter
    public static class deleteCommentDTO{

        Long commentId;
        String content;


        public deleteCommentDTO(Comment comment){
            this.commentId = comment.getId();
            this.content = comment.getContent();
        }
    }

    @Getter
    @Setter
    public static class updateCommentDTO{

        Long commentId;
        String newContent;


        public updateCommentDTO(Comment comment){
            this.commentId = comment.getId();
            this.newContent = comment.getContent();
        }
    }

}
