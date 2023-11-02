package com.kakao.techcampus.wekiki.group.service;

import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.error.exception.Exception500;
import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.dto.GroupRequestDTO;
import com.kakao.techcampus.wekiki.group.dto.GroupResponseDTO;
import com.kakao.techcampus.wekiki.group.repository.GroupJPARepository;
import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.repository.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.domain.member.InactiveGroupMember;
import com.kakao.techcampus.wekiki.group.domain.OfficialGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.domain.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import com.kakao.techcampus.wekiki.post.Post;
import com.kakao.techcampus.wekiki.post.PostJPARepository;
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
            ActiveGroupMember groupMember = buildGroupMember(member, group, requestDTO.groupNickName());

            // Entity 저장
            groupJPARepository.save(group);
            groupMemberJPARepository.save(groupMember);

            // return
            return new GroupResponseDTO.CreateUnOfficialGroupResponseDTO(group);

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
    public GroupResponseDTO.SearchGroupDTO searchGroupByKeyword(String keyword) {
        try {
            Pageable pageable = PageRequest.of(0, 10);

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
    public GroupResponseDTO.SearchOfficialGroupResponseDTO searchOfficialGroupByKeyword(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // 비공식 공개 그룹 리스트
            Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword, pageable);

            return new GroupResponseDTO.SearchOfficialGroupResponseDTO(officialGroups);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        비공식 공개 그룹 추가 리스트
     */
    public GroupResponseDTO.SearchUnOfficialGroupResponseDTO searchUnOfficialGroupByKeyword(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // 비공식 공개 그룹 리스트
            Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword, pageable);

            return new GroupResponseDTO.SearchUnOfficialGroupResponseDTO(unOfficialOpenedGroups);

        } catch (Exception e) {
            throw new Exception500("서버 에러가 발생했습니다.");
        }
    }

    /*
        비공식 공개 그룹 상세 정보 조회
     */
    public GroupResponseDTO.SearchGroupInfoDTO getGroupInfo(Long groupId) {
        try {
            UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId)
                    .orElseThrow(() -> new Exception404("그룹을 찾을 수 없습니다."));

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
    public void groupEntry(Long groupId, GroupRequestDTO.GroupEntryRequestDTO requestDTO) {
        try {
            UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId)
                    .orElseThrow(() -> new Exception404("그룹을 찾을 수 없습니다."));

            // 틀린 경우, 에러 핸들링
            if(!group.getEntrancePassword().equals(requestDTO.entrancePassword())) {
                throw new Exception400("비밀번호가 틀렸습니다.");
            }

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
        // 회원 정보 확인
        Member member = getMemberById(memberId);

        // 그룹 정보 확인
        Group group = getGroupById(groupId);

        // 이미 가입한 상태일 시 예외 처리
        if (groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupId(memberId, groupId).isPresent()) {
            throw new Exception400("이미 가입된 회원입니다.");
        }

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
    }

    /*
        그룹 내 그룹원 리스트 조회
     */
    public GroupResponseDTO.GetGroupMembersResponseDTO getGroupMembers(Long groupId, Long memberId) {
        try {
            Group group = getGroupById(groupId);
            Member member = getMemberById(memberId);

            if(groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupId(memberId, groupId).isEmpty()) {
                throw new Exception400("해당 그룹에 대한 권한이 없습니다.");
            }

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
            ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupId(memberId, groupId)
                    .orElseThrow(() -> new Exception404("해당 그룹의 회원이 아닙니다"));

            // 해당 멤버의 Post 기록 정보 확인(History에서 가져옴)
            Pageable pageable = PageRequest.of(0, 10);
            Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember, pageable);

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
    @Transactional
    public GroupResponseDTO.MyGroupHistoryResponseDTO getMyGroupHistory(Long groupId, Long memberId, int page, int size) {
        try {
            // 그룹 멤버 확인
            ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupId(memberId, groupId)
                    .orElseThrow(() -> new Exception404("해당 그룹의 회원이 아닙니다"));

            Pageable pageable = PageRequest.of(page, size);
            Page<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember, pageable);

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
            ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupId(memberId, groupId)
                    .orElseThrow(() -> new Exception404("해당 그룹의 회원이 아닙니다"));

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
            // 그룹 멤버 확인
            ActiveGroupMember activeGroupMember = groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupId(memberId, groupId)
                    .orElseThrow(() -> new Exception404("해당 그룹의 회원이 아닙니다"));

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
}
