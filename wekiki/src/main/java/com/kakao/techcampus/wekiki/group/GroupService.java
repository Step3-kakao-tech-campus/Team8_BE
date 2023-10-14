package com.kakao.techcampus.wekiki.group;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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

        // Group 생성
        Group group = switch (requestDTO.groupType()) {
            case UNOFFICIAL_CLOSED -> buildUnOfficialClosedGroup(requestDTO);
            case UNOFFICIAL_OPENED -> buildUnOfficialOpenedGroup(requestDTO);
            default ->
                // TODO: 예외 처리 구현
                    throw new IllegalArgumentException("Invalid groupType");
        };

        // MemberId로부터 Member 찾기
        // TODO: 예외 처리 구현
        Member member = getMemberById(memberId);
        // GroupMember 생성
        ActiveGroupMember groupMember = buildGroupMember(member, group, requestDTO.groupNickName());

        // Entity 저장
        groupJPARepository.save(group);
        groupMemberJPARepository.save(groupMember);

        // return
        return new CreateUnOfficialGroupResponseDTO(group);
    }

    /*
        비공식 비공개 그룹 생성 후 반환
     */
    protected UnOfficialClosedGroup buildUnOfficialClosedGroup(CreateUnOfficialGroupRequestDTO requestDTO) {
        // Group 생성
        return UnOfficialClosedGroup.unOfficialClosedGroupBuilder()
                .groupName(requestDTO.groupName())
                .groupProfileImage(requestDTO.groupImage())
                .memberCount(1)
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
                .memberCount(1)
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
        - 이름 정렬 후 반환
     */
    public SearchGroupDTO searchGroupByKeyword(String keyword) {

        // 공식 그룹 리스트
        Page<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword);
        // 비공식 공개 그룹 리스트
        Page<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword);
        
        // TODO: 페이지네이션 필요

        return new SearchGroupDTO(officialGroups, unOfficialOpenedGroups);
    }

    /*
        초대 링크 확인
     */
    public GetInvitationLinkResponseDTO getGroupInvitationLink(Long groupId) {

        Invitation invitation = groupJPARepository.findInvitationLinkByGroupId(groupId);

        return new GetInvitationLinkResponseDTO(invitation);
    }

    /*
        비공식 비공개 그룹 초대 링크 확인
        - 확인 후 해당하는 그룹 Id 반환
        - 가입 url 생성 후 그룹 가입 API
     */
    public ValidateInvitationResponseDTO ValidateInvitation(String invitationLink) {

        String[] invitation = invitationLink.split("/");

        Long groupId = Long.parseLong(invitation[0]);
        String invitationCode = invitation[1];

        UnOfficialClosedGroup group = groupJPARepository.findUnOfficialClosedGroupById(groupId);

        // 초대 코드가 틀린 경우
        if(!group.getInvitation().getInvitationCode().equals(invitationCode)) {
            // 예외 처리
        }

        return new ValidateInvitationResponseDTO(groupId);
    }

    /*
        비공식 공개 그룹 상세 정보 조회
     */
    public SearchGroupInfoDTO getGroupInfo(Long groupId) {
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

        return new SearchGroupInfoDTO(group);
    }

    public void groupEntry(Long groupId, String entrancePassword) {
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

        // 틀린 경우, 에러 핸들링
        if(group.getEntrancePassword().equals(entrancePassword)) {
            // TODO: 예외 처리
        }
    }

    /*
        그룹 참가 (공통 부분)
     */
    public void joinGroup(Long groupId, Long memberId, JoinGroupRequestDTO requestDTO) {
        // 회원 정보 확인
        Member member = getMemberById(memberId);
        
        // 그룹 정보 확인
        // TODO: Redis 활용
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

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
        그룹 내 마이 페이지
     */
    public MyGroupInfoResponseDTO getMyGroupInfo(Long groupId, Long memberId) {
        // 회원 정보 확인
        Member member = getMemberById(memberId);

        // 그룹 정보 확인
        // TODO: Redis 활용
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

        // 그룹 멤버 확인
        ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberAndGroup(member, group);

        // 해당 멤버의 Post 기록 정보 확인(History에서 가져옴)
        // TODO: 페이지네이션 필요
        List<History> myHistoryList = historyJPARepository.findAllByGroupMember(groupMember);

        // 그룹 이름, 현재 닉네임, Post 기록 정보를 담은 responseDTO 반환
        return new MyGroupInfoResponseDTO(group, groupMember, myHistoryList);
    }

    /*
        그룹 내 마이 페이지 정보 수정
     */
    public void updateMyGroupPage(Long groupId, Long memberId, UpdateMyGroupPageDTO requestDTO) {
        // 회원 정보 확인
        Member member = getMemberById(memberId);

        // 그룹 정보 확인
        // TODO: Redis 활용
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

        // 그룹 멤버 확인
        // TODO: Redis 활용
        ActiveGroupMember groupMember = groupMemberJPARepository.findActiveGroupMemberByMemberAndGroup(member, group);

        // 그룹 닉네임 변경
        groupMember.update(requestDTO.groupNickName());

        // 저장
        groupMemberJPARepository.save(groupMember);
    }

    /*
        그룹 탈퇴
     */
    public void leaveGroup(Long groupId, Long memberId) {
        // 회원 정보 확인
        Member member = getMemberById(memberId);

        // 그룹 정보 확인
        // TODO: Redis 활용
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);

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
    }

    public Member getMemberById(Long memberId) {
        return memberJPARepository.findById(memberId).orElse(null);
    }
}
