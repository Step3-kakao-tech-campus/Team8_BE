package com.kakao.techcampus.wekiki.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_tb")
public class Member {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "binary(16)")
    private UUID id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime created_at;
    @Enumerated(value = EnumType.STRING)
    Authority authority;

    @Builder
    public Member(String name, String email, String password, LocalDateTime created_at, Authority authority) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.created_at = created_at;
        this.authority = authority;
    }
}
