package com.kakao.techcampus.wekiki.group.service;

import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.utils.redis.RedisUtils;
import com.kakao.techcampus.wekiki.group.dto.GroupResponseDTO;
import com.kakao.techcampus.wekiki.group.domain.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class InvitationService {

    private final RedisUtils redisUtils;

    private static final String GROUP_ID_PREFIX = "group_id:";
    private static final String INVITATION_PREFIX = "invitation:";

    /*
        InvitationCode 확인
        - 기존에 있으면 Code 반환
        - 기존에 없으면 새로 생성
        - new GetInvitationLinkResponseDTO(Invitation invitation)
     */
    public GroupResponseDTO.GetInvitationLinkResponseDTO getGroupInvitationCode(Long groupId) {

        String groupKey = GROUP_ID_PREFIX + groupId;

        // 기존 초대 링크 여부 확인
        Invitation invitation = (Invitation) redisUtils.getValues(groupKey);

        // 없으면 새로 생성 후 Redis 저장
        /*
            redisInvitation : groupId로 Invitation 저장
            redisGroupId : invitation.code로 groupId 저장
         */
        if(invitation == null) {
            invitation = Invitation.create(groupId);
            redisUtils.setInvitationValues(groupKey, invitation, invitation.remainDuration(LocalDateTime.now()));
            redisUtils.setGroupIdValues(INVITATION_PREFIX + invitation.code(), groupId, invitation.remainDuration(LocalDateTime.now()));
        }

        // 있으면 해당 초대 링크로 requestDTO 생성
        return new GroupResponseDTO.GetInvitationLinkResponseDTO(invitation);
    }

    // 초대 링크를 통한 접근 시 유효한 초대 링크 확인, 해당 그룹으로 연결
    public GroupResponseDTO.ValidateInvitationResponseDTO validateInvitation(String invitationLink) {

        // 초대 링크를 통해 groupId와 invitation 찾기
        // groupId는 현재 Integer 타입
        Object groupId = Optional.ofNullable(redisUtils.getValues(INVITATION_PREFIX + invitationLink))
                .orElseThrow(() -> new Exception404("잘못된 접근입니다."));

        // 초대 링크 기간 확인
        Invitation invitation = (Invitation) redisUtils.getValues(GROUP_ID_PREFIX + groupId);

        if(invitation == null || !invitation.isUsableAt(LocalDateTime.now())) {
            throw new Exception404("이미 만료된 초대 링크입니다.");
        }

        return new GroupResponseDTO.ValidateInvitationResponseDTO(((Integer) groupId).longValue());
    }

    /*
        초대 링크의 만료를 확인하고 삭제
        - redisInvitation, redisGroupId는 같은 수명을 가지기 때문에 하나만 확인해서 둘 다 삭제
     */
    private void removeExpiredInvitations() {
        Set<String> keys = redisUtils.getKeys(GROUP_ID_PREFIX + "*");

        for (String key : keys) {
            Invitation invitation = (Invitation) redisUtils.getValues(key);

            // 초대 링크 수명이 다하면 삭제
            if (invitation != null && !invitation.isUsableAt(LocalDateTime.now())) {
                redisUtils.deleteValues(key);
                redisUtils.deleteValues(INVITATION_PREFIX + invitation.code());
            }
        }
    }

    // 2주마다 화요일 새벽 4시에 Redis 검사 후 만료된 초대 링크 확인하고 삭제
    @Scheduled(cron = "0 0 4 */14 * 2")
    protected void removeExpiredInvitationsJob() {
        removeExpiredInvitations();
    }
}
