package com.kakao.techcampus.wekiki.comment;

import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_tb")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    @ManyToOne
    private Post post;
    private String content;
    private LocalDateTime created_at;

    @Builder
    public Comment(Long id, Member member, Post post, String content, LocalDateTime created_at) {
        this.id = id;
        this.member = member;
        this.post = post;
        this.content = content;
        this.created_at = created_at;
    }
}