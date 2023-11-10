package com.kakao.techcampus.wekiki.group.service;

import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.utils.redis.RedisUtils;
import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.dto.GroupResponseDTO;
import com.kakao.techcampus.wekiki.group.domain.Invitation;
import com.kakao.techcampus.wekiki.group.repository.GroupJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvitationService {

    private final RedisUtils redisUtils;

    private final GroupJPARepository groupJPARepository;

    private static final String GROUP_ID_PREFIX = "group_id:";
    private static final String INVITATION_PREFIX = "invitation:";
    private static final String MEMBER_ID_PREFIX = "member_id:";
    private static final long MEMBER_ID_LIFETIME = 3L;

    /*
        InvitationCode 확인
        - 기존에 있으면 Code 반환
        - 기존에 없으면 새로 생성
        - new GetInvitationLinkResponseDTO(Invitation invitation)
     */
    public GroupResponseDTO.GetInvitationLinkResponseDTO getGroupInvitationCode(Long groupId) {

        groupJPARepository.findById(groupId).orElseThrow(() -> {
            log.info("존재하지 않는 그룹 접근 " + groupId + " 번 그룹");
            throw new Exception404("해당 그룹을 찾을 수 없습니다.");
        });

        log.debug("그룹 조회 완료");

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

            log.debug("invitation Redis 저장 완료");
        }

        log.debug("invitation 조회 완료");

        // 있으면 해당 초대 링크로 requestDTO 생성
        return new GroupResponseDTO.GetInvitationLinkResponseDTO(groupId, invitation);
    }

    // 초대 링크를 통한 접근 시 유효한 초대 링크 확인, 해당 그룹으로 연결
    public GroupResponseDTO.ValidateInvitationResponseDTO validateInvitation(String invitationLink, Long memberId) {

        // 초대 링크를 통해 groupId와 invitation 찾기
        // groupId는 현재 Integer 타입
        Object groupId = Optional.ofNullable(redisUtils.getValues(INVITATION_PREFIX + invitationLink))
                .orElseThrow(() -> {
                    log.info(invitationLink + "에 해당하는 그룹이 조회 불가");
                    throw new Exception404("존재하지 않는 초대 링크입니다.");
                });
        
        log.debug("초대링크에 해당하는 그룹 조회 완료");

        // 초대 링크 기간 확인
        Invitation invitation = (Invitation) redisUtils.getValues(GROUP_ID_PREFIX + groupId);

        if(invitation == null || !invitation.isUsableAt(LocalDateTime.now())) {
            log.info(groupId + " 번 그룹의 만료된 초대 링크 사용");
            throw new Exception404("이미 만료된 초대 링크입니다.");
        }
        
        log.debug("해당 그룹의 초대 링크 유효 기간 확인 완료");

        Long lGroupId = ((Integer) groupId).longValue();

        Group group = groupJPARepository.findById(lGroupId).orElseThrow(() -> {
            log.info(groupId + " 번 그룹은 존재하지 않는 그룹");
            throw new Exception400("해당 그룹은 존재하지 않습니다.");
        });
        
        log.debug("그룹 조회 완료");

        redisUtils.setGroupIdValues(MEMBER_ID_PREFIX + memberId, lGroupId, Duration.ofHours(MEMBER_ID_LIFETIME));
        
        log.debug("회원 가입 권한 저장 완료");

        return new GroupResponseDTO.ValidateInvitationResponseDTO(group);
    }

    /*
        초대 링크의 만료를 확인하고 삭제
        - redisInvitation, redisGroupId는 같은 수명을 가지기 때문에 하나만 확인해서 둘 다 삭제
     */
    private void removeExpiredInvitations() {
        Set<String> keys = redisUtils.getKeys(GROUP_ID_PREFIX + "*");
        
        log.debug("Redis 내의 초대 링크 관련 key 리스트 조회 완료");

        for (String key : keys) {
            Invitation invitation = (Invitation) redisUtils.getValues(key);

            // 초대 링크 수명이 다하면 삭제
            if (invitation != null && !invitation.isUsableAt(LocalDateTime.now())) {
                redisUtils.deleteValues(key);
                redisUtils.deleteValues(INVITATION_PREFIX + invitation.code());
            }
        }
        
        log.debug("Redis 내의 만료된 초대 링크 관련 값들 삭제 완료");
    }

    // 2주마다 화요일 새벽 4시에 Redis 검사 후 만료된 초대 링크 확인하고 삭제
    @Scheduled(cron = "0 0 4 */14 * 2")
    protected void removeExpiredInvitationsJob() {
        removeExpiredInvitations();
    }
}
