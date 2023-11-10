package com.kakao.techcampus.wekiki.group.service;

import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.utils.redis.RedisUtils;
import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.GroupMember;
import com.kakao.techcampus.wekiki.group.dto.GroupRequestDTO;
import com.kakao.techcampus.wekiki.group.dto.GroupResponseDTO;
import com.kakao.techcampus.wekiki.group.repository.GroupJPARepository;
import com.kakao.techcampus.wekiki.group.repository.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.domain.OfficialGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import com.kakao.techcampus.wekiki.page.PageInfo;
import com.kakao.techcampus.wekiki.page.PageJPARepository;
import com.kakao.techcampus.wekiki.post.Post;
import com.kakao.techcampus.wekiki.post.PostJPARepository;
import com.kakao.techcampus.wekiki.report.ReportJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GroupService {

    private final RedisUtils redisUtils;

    private final InvitationService invitationService;

    private final GroupJPARepository groupJPARepository;
    private final GroupMemberJPARepository groupMemberJPARepository;
    private final MemberJPARepository memberJPARepository;
    private final PageJPARepository pageJPARepository;
    private final PostJPARepository postJPARepository;
    private final HistoryJPARepository historyJPARepository;
    private final ReportJPARepository reportJPARepository;

    private static final int GROUP_SEARCH_SIZE = 16;
    private static final String MEMBER_ID_PREFIX = "member_id:";
    private static final long MEMBER_ID_LIFETIME = 3L;

    /*
        비공식 그룹 생성
     */
    @Transactional
    public GroupResponseDTO.CreateUnOfficialGroupResponseDTO createUnOfficialGroup(GroupRequestDTO.CreateUnOfficialGroupRequestDTO requestDTO, Long memberId) {
        
        // Group 생성
        Group group = switch (requestDTO.groupType()) {
            case UNOFFICIAL_CLOSED -> buildUnOfficialClosedGroup(requestDTO);
            case UNOFFICIAL_OPENED -> buildUnOfficialOpenedGroup(requestDTO);
            default -> {
                log.info("그룹 생성 실패 " + memberId + " 번 회원");
                throw new Exception400("유효하지 않은 그룹 유형입니다.");
            }
        };

        log.debug("그룹 생성 완료");

        // MemberId로부터 Member 찾기
        Member member = getMemberById(memberId);
        // GroupMember 생성
        GroupMember groupMember = buildGroupMember(member, group, requestDTO.groupNickName());
        
        log.debug("GroupMember 생성 완료");

        // Entity 저장
        saveGroupMember(member, group, groupMember);
        
        log.debug("Entity 저장 완료");

        // return
        return new GroupResponseDTO.CreateUnOfficialGroupResponseDTO(group, invitationService.getGroupInvitationCode(group.getId()));
    }


    /*
        비공식 비공개 그룹 생성 후 반환
     */
    protected UnOfficialClosedGroup buildUnOfficialClosedGroup(GroupRequestDTO.CreateUnOfficialGroupRequestDTO requestDTO) {
        // Group 생성
        return UnOfficialClosedGroup.unOfficialClosedGroupBuilder()
                .groupName(requestDTO.groupName())
                .groupProfileImage(requestDTO.groupImage())
                .created_at(LocalDateTime.now())
                .build();
    }

    /*
        비공식 공개 그룹 생성 후 반환
     */
    protected UnOfficialOpenedGroup buildUnOfficialOpenedGroup(GroupRequestDTO.CreateUnOfficialGroupRequestDTO requestDTO) {
        // Group 생성
        return UnOfficialOpenedGroup.unOfficialOpenedGroupBuilder()
                .groupName(requestDTO.groupName())
                .groupProfileImage(requestDTO.groupImage())
                .created_at(LocalDateTime.now())
                .introduction(requestDTO.introduction())
                .entranceHint(requestDTO.entranceHint())
                .entrancePassword(requestDTO.entrancePassword())
                .build();
    }

    /*
        GroupMember 생성 후 반환
     */
    protected GroupMember buildGroupMember(Member member, Group group, String groupNickName) {
        return GroupMember.builder()
                .member(member)
                .group(group)
                .nickName(groupNickName)
                .created_at(LocalDateTime.now())
                .build();
    }

    /*
        공식 그룹 리스트, 비공식 공개 그룹 리스트 그룹
        - keyword가 포함된 모든 그룹 그룹별 리스트
     */
    public GroupResponseDTO.SearchGroupDTO searchGroupByKeyword(String keyword) {
        Pageable pageable = PageRequest.of(0, GROUP_SEARCH_SIZE);

        // 공식 그룹 리스트
        Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);
        // 비공식 공개 그룹 리스트
        Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);
        
        log.debug("그룹 리스트 조회 완료");

        return new GroupResponseDTO.SearchGroupDTO(officialGroups, unOfficialOpenedGroups);
    }

    /*
        공식 그룹 추가 리스트
     */
    public GroupResponseDTO.SearchOfficialGroupResponseDTO searchOfficialGroupByKeyword(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, GROUP_SEARCH_SIZE);

        // 공식 그룹 리스트
        Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);

        log.debug("공식 그룹 리스트 조회 완료");

        if (officialGroups.isEmpty()) {
            log.info("공식 그룹 마지막 페이지");
        }

        return new GroupResponseDTO.SearchOfficialGroupResponseDTO(officialGroups);
    }

    /*
        비공식 공개 그룹 추가 리스트
     */
    public GroupResponseDTO.SearchUnOfficialGroupResponseDTO searchUnOfficialGroupByKeyword(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, GROUP_SEARCH_SIZE);

        // 비공식 공개 그룹 리스트
        Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);

        log.debug("공개 그룹 리스트 조회 완료");

        if (unOfficialOpenedGroups.isEmpty()) {
            log.info("비공식 공개 그룹 마지막 페이지");
        }

        return new GroupResponseDTO.SearchUnOfficialGroupResponseDTO(unOfficialOpenedGroups);
    }

    /*
        그룹 상세 정보 조회
     */
    public GroupResponseDTO.SearchGroupInfoDTO getGroupInfo(Long groupId) {
        Group group = getGroupById(groupId);
        
        log.debug("그룹 상세 정보 조회 완료");

        return new GroupResponseDTO.SearchGroupInfoDTO(group);
    }


    /*
        비공식 공개 그룹 입장
     */
    public void groupEntry(Long groupId, Long memberId, GroupRequestDTO.GroupEntryRequestDTO requestDTO) {
        getGroupById(groupId);
        getMemberById(memberId);

        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId)
                .orElseThrow(() -> {
                    log.info("그룹 조회 실패 " + memberId + " 번 회원, " + groupId + " 번 그룹");
                    return new Exception404("그룹을 찾을 수 없습니다.");
                });

        log.debug("그룹 조회 완료");

        // 틀린 경우, 에러 핸들링
        if(!group.getEntrancePassword().equals(requestDTO.entrancePassword())) {
            log.info("비밀번호 불일치 " + memberId + " 번 회원, " + groupId + " 번 그룹");
            throw new Exception400("비밀번호가 틀렸습니다.");
        }
        
        log.debug("비밀번호 인증 완료");

        redisUtils.setGroupIdValues(MEMBER_ID_PREFIX + memberId, groupId, Duration.ofHours(MEMBER_ID_LIFETIME));
    }

    /*
        그룹 참가 (공통 부분)
     */
    @Transactional
    public void joinGroup(Long groupId, Long memberId, GroupRequestDTO.JoinGroupRequestDTO requestDTO) {
        // 회원 정보 확인
        Member member = getMemberById(memberId);
        // 그룹 정보 확인
        Group group = getGroupById(groupId);

        checkJoinPermission(memberId, groupId);
        
        log.debug("가입 권한 확인 완료");

        String groupNickName = requestDTO.nickName();

        GroupMember wasGroupMember = groupMemberJPARepository.findGroupMemberByMemberIdAndGroupId(memberId, groupId);

        if(wasGroupMember != null) {
            // 이미 가입한 상태일 시 예외 처리
            if(wasGroupMember.isActiveStatus()) {
                log.info("이미 가입된 회원 " + memberId + " 번 회원, " + groupId + " 번 그룹");
                throw new Exception400("이미 가입된 회원입니다.");
            }

            wasGroupMember.changeStatus();
            wasGroupMember.update(requestDTO.nickName());

            // GroupMember 저장
            saveGroupMember(member, group, wasGroupMember);
        } else {
            groupNickNameCheck(groupId, groupNickName);
            
            log.debug("그룹 닉네임 중복 확인 완료");

            saveGroupMember(member, group, buildGroupMember(member, group, groupNickName));
        }
        
        log.debug("그룹 참가 완료");

        redisUtils.deleteValues(MEMBER_ID_PREFIX + memberId);
    }

    private void saveGroupMember(Member member, Group group, GroupMember groupMember) {
        // GroupMember 저장
        group.addGroupMember(groupMember);
        member.getGroupMembers().add(groupMember);

        groupJPARepository.save(group);
        memberJPARepository.save(member);
        groupMemberJPARepository.save(groupMember);
        
        log.debug("그룹 멤버 저장 완료");
    }

    protected void checkJoinPermission(Long memberId, Long groupId) {
        Object oGroupId = Optional.ofNullable(redisUtils.getValues(MEMBER_ID_PREFIX + memberId))
                .orElseThrow(() -> {
                    log.info("그룹 가입 권한 없음 " + memberId + " 번 회원, " + groupId + " 번 그룹");
                    throw new Exception404("해당 그룹에 가입할 수 없습니다.");
                });

        Long lGroupId = ((Integer) oGroupId).longValue();

        if(!lGroupId.equals(groupId)) {
            log.info("현재 그룹에 대한 가입 권한 없음 " + memberId + " 번 회원, " + groupId + " 번 그룹");
            throw new Exception400("현재 그룹에 가입할 수 없습니다.");
        }
    }

    /*
        그룹 내 그룹원 리스트 조회
     */
    public GroupResponseDTO.GetGroupMembersResponseDTO getGroupMembers(Long groupId, Long memberId) {
        Group group = getGroupById(groupId);
        getActiveGroupMember(groupId, memberId);
        
        log.debug("그룹원 리스트 조회 완료");

        return new GroupResponseDTO.GetGroupMembersResponseDTO(group);
    }

    /*
        그룹 내 마이 페이지
     */
    public GroupResponseDTO.MyGroupInfoResponseDTO getMyGroupInfo(Long groupId, Long memberId) {
        // 그룹 멤버 확인
        GroupMember groupMember = getActiveGroupMember(groupId, memberId);

        // 해당 멤버의 Post 기록 정보 확인(History에서 가져옴)
        Pageable pageable = PageRequest.of(0, 10);
        Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember.getId(), pageable);
        
        log.debug("히스토리 조회 완료");

        // 그룹 이름, 현재 닉네임, Post 기록 정보를 담은 responseDTO 반환
        return new GroupResponseDTO.MyGroupInfoResponseDTO(groupMember.getGroup(), groupMember, myHistoryList);
    }

    /*
        내 문서 기여 목록 전체 보기
     */
    public GroupResponseDTO.MyGroupHistoryResponseDTO getMyGroupHistory(Long groupId, Long memberId, int page, int size) {
        // 그룹 멤버 확인
        GroupMember groupMember = getActiveGroupMember(groupId, memberId);

        Pageable pageable = PageRequest.of(page, size);
        Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember.getId(), pageable);

        log.debug("히스토리 조회 완료");

        return new GroupResponseDTO.MyGroupHistoryResponseDTO(myHistoryList);
    }


    /*
        그룹 내 마이 페이지 정보 수정
     */
    @Transactional
    public void updateMyGroupPage(Long groupId, Long memberId, GroupRequestDTO.UpdateMyGroupPageDTO requestDTO) {
        // 그룹 멤버 확인
        GroupMember groupMember = getActiveGroupMember(groupId, memberId);

        // 변경할 닉네임 확인
        String newNickName = requestDTO.groupNickName();

        // 빈칸일 경우 예외 처리
        if(newNickName.isEmpty()) {
            log.info("공백 닉네임 " + memberId + " 번 회원, " + groupId + " 번 그룹");
            throw new Exception400("공백은 닉네임이 될 수 없습니다.");
        }

        // 기존 닉네임과 같은 경우 예외 처리
        if(groupMember.getNickName().equals(newNickName)) {
            log.info("기존과 동일한 닉네임 " + memberId + " 번 회원, " + groupId + " 번 그룹");
            throw new Exception400("기존 닉네임과 같은 닉네임입니다.");
        }

        // 이미 있는 경우 예외 처리
        groupNickNameCheck(groupId, requestDTO.groupNickName());

        // 그룹 닉네임 변경
        groupMember.update(requestDTO.groupNickName());

        // 저장
        groupMemberJPARepository.save(groupMember);

        log.debug("그룹 닉네임 변경 완료");
    }


    /*
        그룹 탈퇴
     */
    @Transactional
    public void leaveGroup(Long groupId, Long memberId) {
        GroupMember groupMember = getActiveGroupMember(groupId, memberId);

        Group group = getGroupById(groupId);

        if(group.getMemberCount() != 1) {
            group.minusMemberCount();
            groupMember.changeStatus();
            groupMember.update("알수없음");

            Member member = getMemberById(memberId);
            member.getGroupMembers().remove(groupMember);
            memberJPARepository.save(member);

            groupJPARepository.save(group);
            groupMemberJPARepository.save(groupMember);
            
            log.debug("그룹 탈퇴 완료");

        } else {
            deleteGroup(group);
        }
    }

    private void deleteGroup(Group group) {
        List<PageInfo> pageList = pageJPARepository.findAllByGroupId(group.getId());

        for(PageInfo pageInfo : pageList) {
            for(Post post : pageInfo.getPosts()) {
                reportJPARepository.deleteReportsByHistoryInQuery(post.getHistorys());

                log.debug(post.getId() + " 번 post의 모든 report 삭제 완료");
            }
        }

        pageJPARepository.deleteAll(pageList);

        for(GroupMember groupMember : group.getGroupMembers()) {
            Member member = groupMember.getMember();
            member.getGroupMembers().remove(groupMember);
            memberJPARepository.save(member);

            log.debug(member.getId() + " 번 회원의 groupMember 삭제 완료");
        }

        groupJPARepository.delete(group);

        log.debug("group 삭제 완료");
    }

    protected Member getMemberById(Long memberId) {
        return memberJPARepository.findById(memberId).orElseThrow(() -> {
            log.info("사용자 조회 불가 " + memberId + " 번 회원");
            throw new Exception404("해당 사용자를 찾을 수 없습니다.");
        });
    }

    protected Group getGroupById(Long groupId) {
        return groupJPARepository.findById(groupId).orElseThrow(() -> {
            log.info("그룹 조회 불가 " + groupId + " 번 그룹");
            throw new Exception404("해당 그룹을 찾을 수 없습니다.");
        });
    }

    private GroupMember getActiveGroupMember(Long groupId, Long memberId) {
        GroupMember groupMember = groupMemberJPARepository.findGroupMemberByMemberIdAndGroupId(memberId, groupId);

        if (groupMember == null) {
            log.info("권한 없는 그룹 접근 " + memberId + " 번 회원, " + groupId + " 번 그룹");
            throw new Exception404("해당 그룹에 속한 회원이 아닙니다.");
        }
        
        if(!groupMember.isActiveStatus()) {
            log.info("탈퇴한 그룹 접근 " + memberId + " 번 회원, " + groupId + " 번 그룹");
            throw new Exception404("해당 그룹에 속한 회원이 아닙니다.");
        }
        
        log.debug("그룹 멤버 조회 완료");

        return groupMember;
    }

    protected void groupNickNameCheck(Long groupId, String groupNickName) {
        if(groupMemberJPARepository.findGroupMemberByNickName(groupId, groupNickName).isPresent()) {
            log.info(groupId + " 번 그룹에서 사용 중인 닉네임");
            throw new Exception400("해당 닉네임은 이미 사용중입니다.");
        }
    }
}
