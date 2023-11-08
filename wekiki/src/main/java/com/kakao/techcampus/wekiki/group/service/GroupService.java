package com.kakao.techcampus.wekiki.group.service;

import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.error.exception.Exception500;
import com.kakao.techcampus.wekiki._core.utils.redis.RedisUtils;
import com.kakao.techcampus.wekiki.comment.Comment;
import com.kakao.techcampus.wekiki.comment.CommentJPARepository;
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
import com.kakao.techcampus.wekiki.report.Report;
import com.kakao.techcampus.wekiki.report.ReportJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private final CommentJPARepository commentJPARepository;
    private final ReportJPARepository reportJPARepository;

    private static final int GROUP_SEARCH_SIZE = 16;
    private static final String MEMBER_ID_PREFIX = "member_id:";
    private static final long MEMBER_ID_LIFETIME = 3L;

    /*
        비공식 그룹 생성
     */
    @Transactional
    public GroupResponseDTO.CreateUnOfficialGroupResponseDTO createUnOfficialGroup(GroupRequestDTO.CreateUnOfficialGroupRequestDTO requestDTO, Long memberId) {
        try {
            // Group 생성
            Group group = switch (requestDTO.groupType()) {
                case UNOFFICIAL_CLOSED -> buildUnOfficialClosedGroup(requestDTO);
                case UNOFFICIAL_OPENED -> buildUnOfficialOpenedGroup(requestDTO);
                default -> throw new Exception400("유효하지 않은 그룹 유형입니다.");
            };

            // MemberId로부터 Member 찾기
            Member member = getMemberById(memberId);
            // GroupMember 생성
            GroupMember groupMember = buildGroupMember(member, group, requestDTO.groupNickName());

            // Entity 저장
            group.addGroupMember(groupMember);
            member.getGroupMembers().add(groupMember);

            groupJPARepository.save(group);
            memberJPARepository.save(member);
            groupMemberJPARepository.save(groupMember);

            // return
            return new GroupResponseDTO.CreateUnOfficialGroupResponseDTO(group, invitationService.getGroupInvitationCode(group.getId()));

        } catch (Exception400 | Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
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
        try {
            Pageable pageable = PageRequest.of(0, GROUP_SEARCH_SIZE);

            // 공식 그룹 리스트
            Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);
            // 비공식 공개 그룹 리스트
            Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);

            return new GroupResponseDTO.SearchGroupDTO(officialGroups, unOfficialOpenedGroups);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        공식 그룹 추가 리스트
     */
    public GroupResponseDTO.SearchOfficialGroupResponseDTO searchOfficialGroupByKeyword(String keyword, int page) {
        try {
            Pageable pageable = PageRequest.of(page, GROUP_SEARCH_SIZE);

            // 비공식 공개 그룹 리스트
            Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);

            if (officialGroups.isEmpty()) {
                throw new Exception404("마지막 페이지입니다.");
            }

            return new GroupResponseDTO.SearchOfficialGroupResponseDTO(officialGroups);

        } catch (Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        비공식 공개 그룹 추가 리스트
     */
    public GroupResponseDTO.SearchUnOfficialGroupResponseDTO searchUnOfficialGroupByKeyword(String keyword, int page) {
        try {
            Pageable pageable = PageRequest.of(page, GROUP_SEARCH_SIZE);

            // 비공식 공개 그룹 리스트
            Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);

            if (unOfficialOpenedGroups.isEmpty()) {
                throw new Exception404("마지막 페이지입니다.");
            }

            return new GroupResponseDTO.SearchUnOfficialGroupResponseDTO(unOfficialOpenedGroups);

        } catch (Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        그룹 상세 정보 조회
     */
    public GroupResponseDTO.SearchGroupInfoDTO getGroupInfo(Long groupId) {
        try {
            Group group = getGroupById(groupId);

            return new GroupResponseDTO.SearchGroupInfoDTO(group);
        } catch (Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }


    /*
        비공식 공개 그룹 입장
     */
    public void groupEntry(Long groupId, Long memberId, GroupRequestDTO.GroupEntryRequestDTO requestDTO) {
        try {
            getGroupById(groupId);
            getMemberById(memberId);

            UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId)
                    .orElseThrow(() -> new Exception404("그룹을 찾을 수 없습니다."));

            // 틀린 경우, 에러 핸들링
            if(!group.getEntrancePassword().equals(requestDTO.entrancePassword())) {
                throw new Exception400("비밀번호가 틀렸습니다.");
            }

            redisUtils.setGroupIdValues(MEMBER_ID_PREFIX + memberId, groupId, Duration.ofHours(MEMBER_ID_LIFETIME));

        } catch (Exception400 | Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        그룹 참가 (공통 부분)
     */
    @Transactional
    public void joinGroup(Long groupId, Long memberId, GroupRequestDTO.JoinGroupRequestDTO requestDTO) {
        try {
            // 회원 정보 확인
            Member member = getMemberById(memberId);
            // 그룹 정보 확인
            Group group = getGroupById(groupId);

            checkJoinPermission(memberId, groupId);

            // 그룹 내 닉네임 중복 예외 처리
            String groupNickName = requestDTO.nickName();

            GroupMember wasGroupMember = groupMemberJPARepository.findGroupMemberByMemberIdAndGroupId(memberId, groupId);

            if(wasGroupMember != null) {
                // 이미 가입한 상태일 시 예외 처리
                if(wasGroupMember.isActiveStatus()) {
                    throw new Exception400("이미 가입된 회원입니다.");
                }

                // 재가입 회원인지 확인
                wasGroupMember.changeStatus();

                // GroupMember 저장
                saveGroupMember(member, group, wasGroupMember);
            } else {
                groupNickNameCheck(groupId, groupNickName);

                saveGroupMember(member, group, buildGroupMember(member, group, groupNickName));
            }

            redisUtils.deleteValues(MEMBER_ID_PREFIX + memberId);

        } catch (Exception400 | Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    private void saveGroupMember(Member member, Group group, GroupMember groupMember) {
        // GroupMember 저장
        group.addGroupMember(groupMember);
        member.getGroupMembers().add(groupMember);

        groupJPARepository.save(group);
        memberJPARepository.save(member);
        groupMemberJPARepository.save(groupMember);
    }

    protected void checkJoinPermission(Long memberId, Long groupId) {
        Object oGroupId = Optional.ofNullable(redisUtils.getValues(MEMBER_ID_PREFIX + memberId))
                .orElseThrow(() -> new Exception404("해당 그룹에 가입할 수 없습니다."));

        Long lGroupId = ((Integer) oGroupId).longValue();

        if(!lGroupId.equals(groupId)) {
            throw new Exception400("현재 그룹에 가입할 수 없습니다.");
        }
    }

    /*
        그룹 내 그룹원 리스트 조회
     */
    public GroupResponseDTO.GetGroupMembersResponseDTO getGroupMembers(Long groupId, Long memberId) {
        try {
            Group group = getGroupById(groupId);
            getActiveGroupMember(groupId, memberId);

            return new GroupResponseDTO.GetGroupMembersResponseDTO(group);

        } catch (Exception400 | Exception404 e) {
            throw e;
        }  catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        그룹 내 마이 페이지
     */
    public GroupResponseDTO.MyGroupInfoResponseDTO getMyGroupInfo(Long groupId, Long memberId) {
        try {
            // 그룹 멤버 확인
            GroupMember groupMember = getActiveGroupMember(groupId, memberId);

            // 해당 멤버의 Post 기록 정보 확인(History에서 가져옴)
            Pageable pageable = PageRequest.of(0, 10);
            Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember.getId(), pageable);

            // 그룹 이름, 현재 닉네임, Post 기록 정보를 담은 responseDTO 반환
            return new GroupResponseDTO.MyGroupInfoResponseDTO(groupMember.getGroup(), groupMember, myHistoryList);

        } catch (Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        내 문서 기여 목록 전체 보기
     */
    public GroupResponseDTO.MyGroupHistoryResponseDTO getMyGroupHistory(Long groupId, Long memberId, int page, int size) {
        try {
            // 그룹 멤버 확인
            GroupMember groupMember = getActiveGroupMember(groupId, memberId);

            Pageable pageable = PageRequest.of(page, size);
            Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember.getId(), pageable);

            return new GroupResponseDTO.MyGroupHistoryResponseDTO(myHistoryList);

        } catch (Exception404 e) {
            throw e;
        }  catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }


    /*
        그룹 내 마이 페이지 정보 수정
     */
    @Transactional
    public void updateMyGroupPage(Long groupId, Long memberId, GroupRequestDTO.UpdateMyGroupPageDTO requestDTO) {
        try {
            // 그룹 멤버 확인
            GroupMember groupMember = getActiveGroupMember(groupId, memberId);

            // 변경할 닉네임 확인
            String newNickName = requestDTO.groupNickName();

            // 빈칸일 경우 예외 처리
            if(newNickName.isEmpty()) {
                throw new Exception400("공백은 닉네임이 될 수 없습니다.");
            }

            // 기존 닉네임과 같은 경우 예외 처리
            if(groupMember.getNickName().equals(newNickName)) {
                throw new Exception400("기존 닉네임과 같은 닉네임입니다.");
            }

            // 이미 있는 경우 예외 처리
            groupNickNameCheck(groupId, requestDTO.groupNickName());

            // 그룹 닉네임 변경
            groupMember.update(requestDTO.groupNickName());

            // 저장
            groupMemberJPARepository.save(groupMember);

        } catch (Exception400 | Exception404 e) {
            throw e;
        }  catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
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

            Member member = getMemberById(memberId);
            member.getGroupMembers().remove(groupMember);
            memberJPARepository.save(member);

            groupJPARepository.save(group);
            groupMemberJPARepository.save(groupMember);

        } else {
            deleteGroup(group);
        }
    }

    private void deleteGroup(Group group) {
        List<GroupMember> groupMemberList = groupMemberJPARepository.findAllByGroupId(group.getId());

        for (GroupMember groupMember : groupMemberList) {
            Long groupMemberId = groupMember.getId();

            List<Report> reportList = reportJPARepository.findAllByFromMemberId(groupMemberId);
            reportJPARepository.deleteAll(reportList);

            List<History> historyList = historyJPARepository.findAllByGroupMemberId(groupMemberId);
            historyJPARepository.deleteAll(historyList);

            List<Comment> commentList = commentJPARepository.findAllByGroupMemberId(groupMemberId);
            commentJPARepository.deleteAll(commentList);

            List<Post> postList = postJPARepository.findAllByGroupMember(groupMemberId);
            postJPARepository.deleteAll(postList);

            List<PageInfo> pageInfoList = pageJPARepository.findAllByGroupMember(group.getId());
            pageJPARepository.deleteAll(pageInfoList);

            Member member = groupMember.getMember();
            member.getGroupMembers().remove(groupMember);
            memberJPARepository.save(member);

            group.removeGroupMember(groupMember);
            groupMemberJPARepository.delete(groupMember);
        }

        groupJPARepository.delete(group);
    }

    protected Member getMemberById(Long memberId) {
        return memberJPARepository.findById(memberId).orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
    }

    protected Group getGroupById(Long groupId) {
        return groupJPARepository.findById(groupId).orElseThrow(() -> new Exception404("해당 그룹을 찾을 수 없습니다."));
    }

    private GroupMember getActiveGroupMember(Long groupId, Long memberId) {
        GroupMember groupMember = groupMemberJPARepository.findGroupMemberByMemberIdAndGroupId(memberId, groupId);

        if (groupMember == null || !groupMember.isActiveStatus()) {
            throw new Exception404("해당 그룹에 속한 회원이 아닙니다.");
        }

        return groupMember;
    }

    protected void groupNickNameCheck(Long groupId, String groupNickName) {
        if(groupMemberJPARepository.findGroupMemberByNickName(groupId, groupNickName).isPresent()) {
            throw new Exception400("해당 닉네임은 이미 사용중입니다.");
        }
    }
}
