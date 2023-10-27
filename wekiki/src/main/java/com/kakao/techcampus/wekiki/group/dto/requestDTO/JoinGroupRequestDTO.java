package com.kakao.techcampus.wekiki.group.dto.requestDTO;

import jakarta.validation.constraints.NotNull;

public record JoinGroupRequestDTO(
        @NotNull(message = "그룹 닉네임을 설정해 주세요.")
        String nickName
) {
}
