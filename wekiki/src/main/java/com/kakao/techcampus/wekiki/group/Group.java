package com.kakao.techcampus.wekiki.group;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "group_tb")
@DiscriminatorColumn(name = "group_type")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String groupProfileImage;
    private int memberCount;
    private LocalDateTime created_at;

    @Builder
    public Group(Long id, String groupName, String groupProfileImage, int memberCount, LocalDateTime created_at) {
        this.id = id;
        this.groupName = groupName;
        this.groupProfileImage = groupProfileImage;
        this.memberCount = memberCount;
        this.created_at = created_at;
    }
}
