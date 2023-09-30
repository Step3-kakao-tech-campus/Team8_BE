package com.kakao.techcampus.wekiki.group.member;

import com.kakao.techcampus.wekiki.group.Group;
import com.kakao.techcampus.wekiki.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_member_tb")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    @ManyToOne
    private Group group;
    private String nickName;
    private int memberLevel;
    private boolean isValid;
    private LocalDateTime created_at;

    @Builder
    public GroupMember(Long id, Member member, Group group, String nickName, int memberLevel, boolean isValid, LocalDateTime created_at) {
        this.id = id;
        this.member = member;
        this.group = group;
        this.nickName = nickName;
        this.memberLevel = memberLevel;
        this.isValid = isValid;
        this.created_at = created_at;
    }
}
