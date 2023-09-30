package com.kakao.techcampus.wekiki.member;

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
@Table(name = "member_tb")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime created_at;
    // private Authority authority;

    @Builder
    public Member(Long id, String name, String email, String password, LocalDateTime created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.created_at = created_at;
    }
}
