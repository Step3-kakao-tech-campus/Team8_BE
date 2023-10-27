package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "group_tb")
@DiscriminatorColumn(name = "group_type", discriminatorType = DiscriminatorType.STRING)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String groupProfileImage;

    @OneToMany(mappedBy = "group")
    private List<GroupMember> groupMembers = new ArrayList<>();

    private int memberCount;
    private LocalDateTime created_at;

    @Builder
    public Group(Long id, String groupName, String groupProfileImage, LocalDateTime created_at) {
        this.id = id;
        this.groupName = groupName;
        this.groupProfileImage = groupProfileImage;
        this.memberCount = 1;
        this.created_at = created_at;
    }
}
