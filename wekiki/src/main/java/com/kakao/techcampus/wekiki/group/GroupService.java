package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.CreateUnOfficialGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.CreateUnOfficialGroupResponseDTO;
import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.openedGroup.UnOfficialOpenedGroup;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}
