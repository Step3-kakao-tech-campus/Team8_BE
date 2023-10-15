package com.kakao.techcampus.wekiki.report;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report_tb")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GroupMember groupMember;
    @ManyToOne
    private Post post;
    private String content;

    @Builder
    public Report(Long id, GroupMember groupMember, Post post, String content) {
        this.id = id;
        this.groupMember = groupMember;
        this.post = post;
        this.content = content;
    }
}
