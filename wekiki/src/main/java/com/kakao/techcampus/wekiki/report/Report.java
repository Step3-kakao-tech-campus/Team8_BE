package com.kakao.techcampus.wekiki.report;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
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
@Table(name = "report_tb")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GroupMember fromMember;

    @ManyToOne
    private History history;
    private String content;

    private LocalDateTime created_at;

    @Builder
    public Report(Long id, GroupMember groupMember, History history, String content, LocalDateTime created_at) {
        this.id = id;
        this.fromMember = groupMember;
        this.history = history;
        this.content = content;
        this.created_at = created_at;
    }


}
