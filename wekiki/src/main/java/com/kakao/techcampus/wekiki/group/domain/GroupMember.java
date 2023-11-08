package com.kakao.techcampus.wekiki.group.domain;

import com.kakao.techcampus.wekiki.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_member_tb")
public class
GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    private String nickName;
    private int memberLevel;
    private LocalDateTime created_at;
    boolean activeStatus;

    @Builder
    public GroupMember(Long id, Member member, Group group, String nickName, LocalDateTime created_at) {
        this.id = id;
        this.member = member;
        this.group = group;
        this.nickName = nickName;
        this.memberLevel = 1;
        this.created_at = created_at;
        this.activeStatus = true;
    }

    public void changeMember(Member member) {
        this.member = member;
    }

    // 그룹 내 정보 변경
    public void update(String groupNickName) {
        this.nickName = groupNickName;
    }

    public void changeStatus() {
        this.activeStatus = !this.activeStatus;
    }
}
