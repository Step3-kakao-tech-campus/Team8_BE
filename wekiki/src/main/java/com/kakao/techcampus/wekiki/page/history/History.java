package com.kakao.techcampus.wekiki.page.history;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.page.Page;
import com.kakao.techcampus.wekiki.page.post.Post;
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
    @ManyToOne
    private GroupMember groupMember;
    @ManyToOne
    private Page page;
    @ManyToOne
    private Post post;
    private String content;
    private LocalDateTime created_at;

    @Builder
    public History(Long id, GroupMember groupMember, Page page, Post post, String content, LocalDateTime created_at) {
        this.id = id;
        this.groupMember = groupMember;
        this.page = page;
        this.post = post;
        this.content = content;
        this.created_at = created_at;
    }
}
