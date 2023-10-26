package com.kakao.techcampus.wekiki.comment;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_tb")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupMember groupMember;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;


    private String content;
    private LocalDateTime created_at;

    @Builder
    public Comment(Long id, GroupMember groupMember, Post post, String content, LocalDateTime created_at) {
        this.id = id;
        this.groupMember = groupMember;
        this.post = post;
        this.content = content;
        this.created_at = created_at;
    }

    public void updateContent(String newContent){
        this.content = newContent;
    }
}