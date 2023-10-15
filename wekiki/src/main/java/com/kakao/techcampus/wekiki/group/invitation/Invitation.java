package com.kakao.techcampus.wekiki.group.invitation;

import jakarta.persistence.Column;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

    @Column(unique = true)
    private String invitationCode;
    private String invitationLink;
    // private LocalDateTime expired_at;

    private static final int INVITE_CODE_LENGTH = 32;
    // private static final Duration DEFAULT_EXPIRED_DAYS = Duration.ofDays(7);

    @Builder
    public Invitation(Long groupId) {
        this.invitationCode = RandomStringUtils.randomAlphanumeric(INVITE_CODE_LENGTH);
        this.invitationLink = groupId + "/" + invitationCode;
        // this.expired_at = LocalDateTime.now().plusMinutes(DEFAULT_EXPIRED_DAYS.toMinutes());
    }
}
