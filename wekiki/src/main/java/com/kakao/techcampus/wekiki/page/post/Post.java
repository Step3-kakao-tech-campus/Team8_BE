package com.kakao.techcampus.wekiki.page.post;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.page.Page;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_tb")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GroupMember groupMember;
    @ManyToOne
    private Page page;
    private String content;
    private LocalDateTime created_at;

    @Builder
    public Post(Long id, GroupMember groupMember, Page page, String content, LocalDateTime created_at) {
        this.id = id;
        this.groupMember = groupMember;
        this.page = page;
        this.content = content;
        this.created_at = created_at;
    }
}
