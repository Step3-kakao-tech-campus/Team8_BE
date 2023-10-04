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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "group_member_tb")
@DiscriminatorColumn(name = "member_status", discriminatorType = DiscriminatorType.STRING)
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

    @Builder
    public GroupMember(Long id, Member member, Group group, String nickName) {
        this.id = id;
        this.member = member;
        this.group = group;
        this.nickName = nickName;
        this.memberLevel = 1;
    }

    // 그룹 내 정보 변경
    public void update(String groupNickName) {
        this.nickName = groupNickName;
    }
}
