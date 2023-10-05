package com.kakao.techcampus.wekiki.post;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.page.PageInfo;
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
    private PageInfo pageInfo;
    private String content;
    private LocalDateTime created_at;

    @Builder
    public Post(Long id, GroupMember groupMember, PageInfo pageInfo, String content, LocalDateTime created_at) {
        this.id = id;
        this.groupMember = groupMember;
        this.pageInfo = pageInfo;
        this.content = content;
        this.created_at = created_at;
    }
}
