package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.UnOfficialGroupRequest;
import com.kakao.techcampus.wekiki.group.unOfficialGroup.UnOfficialGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupRestController {

    private final GroupService groupService;

    /*
        비공식 그룹 생성
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUnOfficialClosedGroup(
            @RequestBody UnOfficialGroupRequest.CreateClosedGroupDTO requestDTO) {

        // JWT Token에서 memberId 획득
        Long tempMemberId = 1L;

        UnOfficialGroupResponse.CreateUnOfficialGroupDTO response = groupService.createUnOfficialClosedGroup(requestDTO, tempMemberId);

        return ResponseEntity.ok().body(ApiUtils.success(response));
    }
    
    /*
        그룹 검색
     */
    
    /*
        특정 공개 그룹 정보 조회
     */
    
    /*
        그룹 참가
     */
    
    /*
        그룹 초대
     */
    
    /*
        그룹 내 본인 정보 조회
     */
    
    /*
        그룹 내 본인 정보 수정
     */
    
    /*
        그룹 탈퇴
     */
}
