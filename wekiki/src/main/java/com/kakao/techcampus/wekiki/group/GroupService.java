package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki.group.member.GroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.UnOfficialGroupRequest;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.UnOfficialGroupResponse;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.closedGroup.UnOfficialClosedGroup;
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
        GroupMember groupMember = GroupMember.builder()
                .member(member)
                .group(closedGroup)
                .nickName(requestDTO.getGroupNickName())
                .memberLevel(1)
                .isValid(true)
                .created_at(LocalDateTime.now())
                .build();

        // TODO: 메인 Page 생성

        // Entity 저장
        groupJPARepository.save(closedGroup);
        groupMemberJPARepository.save(groupMember);

        // return
        return new UnOfficialGroupResponse.CreateUnOfficialGroupDTO(closedGroup);
    }
}
