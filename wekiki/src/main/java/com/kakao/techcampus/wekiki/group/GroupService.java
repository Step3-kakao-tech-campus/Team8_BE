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

    /*
        비공식 비공개 그룹 생성
     */
    @Transactional
    public UnOfficialGroupResponse.CreateUnOfficialGroupDTO createUnOfficialClosedGroup(UnOfficialGroupRequest.CreateClosedGroupDTO requestDTO, Long memberId) {

        // Group 생성
        UnOfficialClosedGroup closedGroup = UnOfficialClosedGroup.unOfficialClosedGroupBuilder()
                .groupName(requestDTO.getGroupName())
                .groupProfileImage(requestDTO.getGroupImage())
                .memberCount(1)
                .created_at(LocalDateTime.now())
                .build();

        // GroupMember 생성
        // MemberId로부터 Member 찾기
        // TODO: 예외 처리
        Member member = memberJPARepository.findById(memberId).orElse(null);
        GroupMember groupMember = buildGroupMember(member, closedGroup, requestDTO.getGroupNickName());


        // TODO: 메인 Page 생성

        // Entity 저장
        groupJPARepository.save(closedGroup);
        groupMemberJPARepository.save(groupMember);

        // return
        return new UnOfficialGroupResponse.CreateUnOfficialGroupDTO(closedGroup);
    }

    /*
        비공식 공개 그룹 생성
     */
    @Transactional
    public UnOfficialGroupResponse.CreateUnOfficialGroupDTO createUnOfficialOpenedGroup(UnOfficialGroupRequest.CreateOpenedGroupDTO requestDTO, Long memberId) {
        // Group 생성
        UnOfficialOpenedGroup openedGroup = UnOfficialOpenedGroup.unOfficialOpenedGroupBuilder()
                .groupName(requestDTO.getGroupName())
                .groupProfileImage(requestDTO.getGroupImage())
                .memberCount(1)
                .created_at(LocalDateTime.now())
                .introduction(requestDTO.getIntroduction())
                .entranceHint(requestDTO.getEntranceHint())
                .entrancePassword(requestDTO.getEntrancePassword())
                .build();

        // GroupMember 생성
        // MemberId로부터 Member 찾기
        // TODO: 예외 처리
        Member member = memberJPARepository.findById(memberId).orElse(null);
        GroupMember groupMember = buildGroupMember(member, openedGroup, requestDTO.getGroupNickName());

        // TODO: 메인 Page 생성

        // Entity 저장
        groupJPARepository.save(openedGroup);
        groupMemberJPARepository.save(groupMember);

        // return
        return new UnOfficialGroupResponse.CreateUnOfficialGroupDTO(openedGroup);
    }

    /*
        GroupMember 생성 후 반환
        - 매개변수 : Member, Group, groupNickName
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
