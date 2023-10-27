package com.kakao.techcampus.wekiki.group.domain;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

public record Invitation(
        Long groupId,
        LocalDateTime expiresAt,
        String code
) {
    private static final long DEFAULT_EXPIRED_DAYS = 7L;
    private static final int INVITE_CODE_LENGTH = 32;

    public static Invitation create(final Long groupId) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(Duration.ofDays(DEFAULT_EXPIRED_DAYS).toMinutes());
        String code = RandomStringUtils.randomAlphanumeric(INVITE_CODE_LENGTH);
        return new Invitation(groupId, expiresAt, code);
    }

    public boolean isUsableAt(final LocalDateTime now) {
        return now.isBefore(expiresAt);
    }

    public Duration remainDuration(final LocalDateTime now) {
        return Duration.between(now, expiresAt);
    }
}
