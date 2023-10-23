package com.kakao.techcampus.wekiki.group.service;

import com.kakao.techcampus.wekiki._core.utils.RedisUtility;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.GetInvitationLinkResponseDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.ValidateInvitationResponseDTO;
import com.kakao.techcampus.wekiki.group.invitation.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class InvitationService {

    private final RedisTemplate<String, Invitation> redisInvitation;

    private static final String INVITATION_PREFIX = "invitation:";

    /*
        InvitationCode 확인
        - 기존에 있으면 Code 반환
        - 기존에 없으면 새로 생성
        - new GetInvitationLinkResponseDTO(Invitation invitation)
     */
    public GetInvitationLinkResponseDTO getGroupInvitationCode(Long groupId) {

        String key = INVITATION_PREFIX + groupId;

        // 기존 초대 링크 여부 확인
        Invitation invitation = redisInvitation.opsForValue().get(key);

        // 없으면 새로 생성 후 Redis 저장
        if(invitation == null) {
            invitation = Invitation.create(groupId);
            redisInvitation.opsForValue().set(key, invitation, invitation.remainDuration(LocalDateTime.now()));
        }

        // 있으면 해당 초대 링크로 requestDTO 생성
        return new GetInvitationLinkResponseDTO(invitation);
    }

    // 초대 링크를 통한 접근 시 유효한 초대 링크 확인, 해당 그룹으로 연결
    public ValidateInvitationResponseDTO ValidateInvitation(String invitationLink) {
        return null;
    }
}
