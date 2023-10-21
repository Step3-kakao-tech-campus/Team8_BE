package com.kakao.techcampus.wekiki.group.invitation;

import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "invitation_tb")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private UnOfficialClosedGroup group;

    @Column(unique = true)
    private String invitationCode;
    private String invitationLink;

    private static final int INVITE_CODE_LENGTH = 32;

    @Builder
    public Invitation(UnOfficialClosedGroup group) {
        this.group = group;
        this.invitationCode = RandomStringUtils.randomAlphanumeric(INVITE_CODE_LENGTH);
        this.invitationLink = group.getId() + "/" + invitationCode;
    }
}
