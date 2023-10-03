package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.CreateUnOfficialGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.CreateUnOfficialGroupResponseDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.SearchGroupDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.SearchGroupInfoDTO;
import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.officialGroup.OfficialGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import lombok.RequiredArgsConstructor;
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

    /*
        비공식 그룹 생성
     */
    @Transactional
    public CreateUnOfficialGroupResponseDTO createUnOfficialGroup(CreateUnOfficialGroupRequestDTO requestDTO, Long memberId) {

        // Group 생성
        Group group;
        
        switch (requestDTO.groupType()) {
            case UNOFFICIAL_CLOSED:
                group = buildUnOfficialClosedGroup(requestDTO);
                break;

            case UNOFFICIAL_OPENED:
                group = buildUnOfficialOpenedGroup(requestDTO);
                break;

            default:
                // TODO: 예외 처리 구현
                throw new IllegalArgumentException("Invalid groupType");
        }

        // MemberId로부터 Member 찾기
        // TODO: 예외 처리 구현
        Member member = memberJPARepository.findById(memberId).orElse(null);
        // GroupMember 생성
        GroupMember groupMember = buildGroupMember(member, group, requestDTO.groupNickName());

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
    protected GroupMember buildGroupMember(Member member, Group group, String groupNickName) {
        return GroupMember.builder()
                .member(member)
                .group(group)
                .nickName(groupNickName)
                .memberLevel(1)
                .isValid(true)
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
        List<OfficialGroup> officialGroups = groupJPARepository.findOfficialGroupsByKeyword(keyword);
        // 비공식 공개 그룹 리스트
        List<UnOfficialOpenedGroup> unOfficialOpenedGroups = groupJPARepository.findUnOfficialOpenedGroupsByKeyword(keyword);

        // 정렬
        officialGroups.sort(Comparator.comparing(OfficialGroup::getGroupName, String.CASE_INSENSITIVE_ORDER));
        unOfficialOpenedGroups.sort(Comparator.comparing(UnOfficialOpenedGroup::getGroupName, String.CASE_INSENSITIVE_ORDER));
        
        // TODO: 페이지네이션 필요

        return new SearchGroupDTO(officialGroups, unOfficialOpenedGroups);
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
        if(group.getEntrancePassword() != entrancePassword) {
            // TODO: 예외 처리
        }
    }

    public void joinGroup(Long groupId, Long memberId) {
        // 회원 정보 확인
        Member member = memberJPARepository.findById(memberId).orElse(null);
        
        // 그룹 정보 확인
        UnOfficialOpenedGroup group = groupJPARepository.findUnOfficialOpenedGroupById(groupId);
        
        // GroupMember 생성
        GroupMember groupMember = buildGroupMember(member, group, member.getName());

        // GroupMember 저장
        groupMemberJPARepository.save(groupMember);
    }
}
