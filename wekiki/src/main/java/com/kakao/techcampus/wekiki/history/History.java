package com.kakao.techcampus.wekiki.history;

import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
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
@Table(name = "history_tb")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupMember groupMember;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POST_ID")
    private Post post;

    private String title;
    private String content;
    private LocalDateTime created_at;

    @Builder
    public History(Long id, Post post){
        this.id = id;
        this.groupMember = post.getGroupMember();
        this.post = post;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.created_at = post.getCreated_at();
    }



    public void updateGroupMember(GroupMember groupMember) {
        this.groupMember = groupMember;
    }
}
