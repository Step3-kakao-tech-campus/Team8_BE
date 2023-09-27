package com.kakao.techcampus.wekiki.page.report;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.page.Page;
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
    private Page page;
    private String content;

    @Builder
    public Report(Long id, GroupMember groupMember, Page page, String content) {
        this.id = id;
        this.groupMember = groupMember;
        this.page = page;
        this.content = content;
    }
}
