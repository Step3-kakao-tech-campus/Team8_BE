package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.UnOfficialGroupRequest;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.UnOfficialGroupResponse;
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

    @Transactional
    public UnOfficialGroupResponse.CreateUnOfficialGroupDTO createUnOfficialGroup(UnOfficialGroupRequest.CreateUnOfficialGroupDTO requestDTO, Long memberId) {

        // MemberId로부터 Member 찾기
        // TODO: 예외 처리 구현
        Member member = memberJPARepository.findById(memberId).orElse(null);

        Group group;
        GroupMember groupMember;

        //
        switch (requestDTO.getGroupType()) {
            case 1:
                group = buildUnOfficialClosedGroup(requestDTO);
                groupMember = buildGroupMember(member, group, requestDTO.getGroupNickName());
                break;

            case 2:
                group = buildUnOfficialOpenedGroup(requestDTO);
                groupMember = buildGroupMember(member, group, requestDTO.getGroupNickName());
                break;

            default:
                // TODO: 예외 처리 구현
                throw new IllegalArgumentException("Invalid groupType");
        }

        // Entity 저장
        groupJPARepository.save(group);
        groupMemberJPARepository.save(groupMember);

        // return
        return new UnOfficialGroupResponse.CreateUnOfficialGroupDTO(group);
    }

    /*
        비공식 비공개 그룹 생성 후 반환
     */
    protected Group buildUnOfficialClosedGroup(UnOfficialGroupRequest.CreateUnOfficialGroupDTO requestDTO) {
        // Group 생성
        return UnOfficialClosedGroup.unOfficialClosedGroupBuilder()
                .groupName(requestDTO.getGroupName())
                .groupProfileImage(requestDTO.getGroupImage())
                .memberCount(1)
                .created_at(LocalDateTime.now())
                .build();
    }

    /*
        비공식 공개 그룹 생성 후 반환
     */
    protected Group buildUnOfficialOpenedGroup(UnOfficialGroupRequest.CreateUnOfficialGroupDTO requestDTO) {
        // Group 생성
        return UnOfficialOpenedGroup.unOfficialOpenedGroupBuilder()
                .groupName(requestDTO.getGroupName())
                .groupProfileImage(requestDTO.getGroupImage())
                .memberCount(1)
                .created_at(LocalDateTime.now())
                .introduction(requestDTO.getIntroduction())
                .entranceHint(requestDTO.getEntranceHint())
                .entrancePassword(requestDTO.getEntrancePassword())
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
