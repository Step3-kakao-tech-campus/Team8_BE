package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.error.exception.Exception500;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.CreateUnOfficialGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.JoinGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.UpdateMyGroupPageDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.*;
import com.kakao.techcampus.wekiki.group.invitation.Invitation;
import com.kakao.techcampus.wekiki.group.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.member.InactiveGroupMember;
import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import com.kakao.techcampus.wekiki.post.Post;
import com.kakao.techcampus.wekiki.post.PostJPARepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupJPARepository groupJPARepository;
    private final GroupMemberJPARepository groupMemberJPARepository;
    private final MemberJPARepository memberJPARepository;
    private final PostJPARepository postJPARepository;
    private final HistoryJPARepository historyJPARepository;

    /*
        비공식 그룹 생성
     */
    @Transactional
    public CreateUnOfficialGroupResponseDTO createUnOfficialGroup(CreateUnOfficialGroupRequestDTO requestDTO, Long memberId) {
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
            ActiveGroupMember groupMember = buildGroupMember(member, group, requestDTO.groupNickName());

            // Entity 저장
            groupJPARepository.save(group);
            groupMemberJPARepository.save(groupMember);

            // return
            return new CreateUnOfficialGroupResponseDTO(group);

        } catch (Exception400 e) {
            throw e;
        } catch (Exception404 e) {
            throw e;
        }   catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }


    /*
        비공식 비공개 그룹 생성 후 반환
     */
    protected UnOfficialClosedGroup buildUnOfficialClosedGroup(CreateUnOfficialGroupRequestDTO requestDTO) {
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
    protected UnOfficialOpenedGroup buildUnOfficialOpenedGroup(CreateUnOfficialGroupRequestDTO requestDTO) {
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
    protected ActiveGroupMember buildGroupMember(Member member, Group group, String groupNickName) {
        return ActiveGroupMember.activeGroupMemberBuilder()
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
    public SearchGroupDTO searchGroupByKeyword(String keyword) {
        try {
            Pageable pageable = PageRequest.of(0, 10);

            // 공식 그룹 리스트
            Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);
            // 비공식 공개 그룹 리스트
            Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);

            return new SearchGroupDTO(officialGroups, unOfficialOpenedGroups);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        공식 그룹 추가 리스트
     */
    public SearchOfficialGroupResponseDTO searchOfficialGroupByKeyword(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // 비공식 공개 그룹 리스트
            Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);

            return new SearchOfficialGroupResponseDTO(officialGroups);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        비공식 공개 그룹 추가 리스트
     */
    public SearchUnOfficialGroupResponseDTO searchUnOfficialGroupByKeyword(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // 비공식 공개 그룹 리스트
            Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);

            return new SearchUnOfficialGroupResponseDTO(unOfficialOpenedGroups);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        초대 링크 확인
     */
    public GetInvitationLinkResponseDTO getGroupInvitationLink(Long groupId) {
        try {
            Invitation invitation = groupJPARepository.findInvitationLinkByGroupId(groupId);

            return new GetInvitationLinkResponseDTO(invitation);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        비공식 비공개 그룹 초대 링크 확인
        - 확인 후 해당하는 그룹 Id 반환
        - 가입 url 생성 후 그룹 가입 API
     */
    public ValidateInvitationResponseDTO ValidateInvitation(String invitationLink) {
        try {
            String[] invitation = invitationLink.split("/");

            Long groupId = Long.parseLong(invitation[0]);
            String invitationCode = invitation[1];

            UnOfficialClosedGroup group = groupJPARepository.findUnOfficialClosedGroupById(groupId);

            // 초대 코드가 틀린 경우
            if (!group.getInvitation().getInvitationCode().equals(invitationCode)) {
                throw new Exception400("초대 코드가 일치하지 않습니다.");
            }

            return new ValidateInvitationResponseDTO(groupId);

        } catch (Exception400 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }


    /*
        비공식 공개 그룹 상세 정보 조회
     */
    public SearchGroupInfoDTO getGroupInfo(Long groupId) {
        try {
            UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);
            return new SearchGroupInfoDTO(group);

        } catch (EntityNotFoundException e) {
            throw new Exception404("그룹을 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }


    /*
        비공식 공개 그룹 입장
     */
    public void groupEntry(Long groupId, String entrancePassword) {
        try {
            UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

            // 틀린 경우, 에러 핸들링
            if(!group.getEntrancePassword().equals(entrancePassword)) {
                throw new Exception400("비밀번호가 틀렸습니다.");
            }

        } catch (Exception400 e) {
            throw e;
        }  catch (EntityNotFoundException e) {
            throw new Exception404("그룹을 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        그룹 참가 (공통 부분)
     */
    @Transactional
    public void joinGroup(Long groupId, Long memberId, JoinGroupRequestDTO requestDTO) {
        try {
            // 회원 정보 확인
            Member member = getMemberById(memberId);

            // 그룹 정보 확인
            // TODO: Redis 활용
            Group group = getGroupById(groupId);

            // 재가입 회원인지 확인
            InactiveGroupMember wasGroupMember = groupMemberJPARepository.findInactiveGroupMemberByMemberAndGroup(member, group);

            // 재가입 회원이면 활성화, 신규 회원이면 새로 생성
            ActiveGroupMember groupMember = wasGroupMember != null ? new ActiveGroupMember(wasGroupMember) : buildGroupMember(member, group, requestDTO.nickName());

            // 그룹 멤버 잔재 삭제
            if(wasGroupMember != null) {
                groupMemberJPARepository.delete(wasGroupMember);
            }

            // GroupMember 저장
            groupMemberJPARepository.save(groupMember);

        } catch (Exception404 e) {
            throw e;
        }  catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        그룹 내 마이 페이지
     */
    public MyGroupInfoResponseDTO getMyGroupInfo(Long groupId, Long memberId) {
        try {
            // 회원 정보 확인
            Member member = getMemberById(memberId);

            // 그룹 정보 확인
            // TODO: Redis 활용
            Group group = getGroupById(groupId);

            // 그룹 멤버 확인
            ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberAndGroup(member, group);

            // 해당 멤버의 Post 기록 정보 확인(History에서 가져옴)
            Pageable pageable = PageRequest.of(0, 10);
            Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember, pageable);

            // 그룹 이름, 현재 닉네임, Post 기록 정보를 담은 responseDTO 반환
            return new MyGroupInfoResponseDTO(group, groupMember, myHistoryList);

        } catch (Exception404 e) {
            throw e;
        }  catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        내 문서 기여 목록 전체 보기
     */
    @Transactional
    public MyGroupHistoryResponseDTO getMyGroupHistory(Long groupId, Long memberId, int page, int size) {
        try {
            // 회원 정보 확인
            Member member = getMemberById(memberId);

            // 그룹 정보 확인
            // TODO: Redis 활용
            Group group = getGroupById(groupId);

            // 그룹 멤버 확인
            ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberAndGroup(member, group);

            Pageable pageable = PageRequest.of(page, size);
            Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember, pageable);

            return new MyGroupHistoryResponseDTO(myHistoryList);

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
    public void updateMyGroupPage(Long groupId, Long memberId, UpdateMyGroupPageDTO requestDTO) {
        try {
            // 회원 정보 확인
            Member member = getMemberById(memberId);

            // 그룹 정보 확인
            // TODO: Redis 활용
            Group group = getGroupById(groupId);

            // 그룹 멤버 확인
            // TODO: Redis 활용
            ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberAndGroup(member, group);

            // 그룹 닉네임 변경
            groupMember.update(requestDTO.groupNickName());

            // 저장
            groupMemberJPARepository.save(groupMember);

        } catch (Exception404 e) {
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
        try {
            // 회원 정보 확인
            Member member = getMemberById(memberId);

            // 그룹 정보 확인
            // TODO: Redis 활용
            Group group = getGroupById(groupId);

            // 그룹 멤버 확인
            // TODO: Redis 활용
            ActiveGroupMember activeGroupMember = groupMemberJPARepository.findActiveGroupMemberByMemberAndGroup(member, group);

            // 탈퇴 그룹 회원 생성
            InactiveGroupMember inactiveGroupMember = new InactiveGroupMember(activeGroupMember);

            // Post의 그룹 멤버 변경
            List<Post> postList = postJPARepository.findAllByGroupMember(activeGroupMember);
            postList.forEach(p -> p.updateGroupMember(inactiveGroupMember));

            // History 그룹 멤버 변경
            List<History> historyList = historyJPARepository.findAllByGroupMember(activeGroupMember);
            historyList.forEach(h -> h.updateGroupMember(inactiveGroupMember));

            // DB 작업
            groupMemberJPARepository.delete(activeGroupMember);
            groupMemberJPARepository.save(inactiveGroupMember);
            historyJPARepository.saveAll(historyList);

        } catch (Exception404 e) {
            throw e;
        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    protected Member getMemberById(Long memberId) {
        return memberJPARepository.findById(memberId).orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
    }

    protected Group getGroupById(Long groupId) {
        return groupJPARepository.findById(groupId).orElseThrow(() -> new Exception404("해당 그룹을 찾을 수 없습니다."));
    }

    // RedisCache를 통한 getMember, getGroup -> 근데 성능 개선에 도움이 될까?
    /*
        protected Member getMemberById(Long memberId) {
            try {
                String memberKey = "member:" + memberId;
                ValueOperations<String, Object> operations = redisTemplate.opsForValue();
                if(redisTemplate.hasKey(memberKey)) {
                    return (Member) operations.get(memberKey);
                } else {
                    Member member = memberJPARepository.findById(memberId).orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
                    operations.set(memberKey, member);
                    return member;
                }
            } catch (Exception e) {
                throw new Exception500("서버 에러가 발생했습니다.");
            }
        }

        protected Group getGroupById(Long groupId) {
            try {
                String groupKey = "group:" + groupId;
                ValueOperations<String, Object> operations = redisTemplate.opsForValue();
                if(redisTemplate.hasKey(groupKey)) {
                    return (Group) operations.get(groupKey);
                } else {
                    Group group = groupJPARepository.findById(groupId).orElseThrow(() -> new Exception404("해당 그룹을 찾을 수 없습니다."));
                    operations.set(groupKey, group);
                    return group;
                }
            } catch (Exception e) {
                throw new Exception500("서버 에러가 발생했습니다.");
            }
        }
     */
}
