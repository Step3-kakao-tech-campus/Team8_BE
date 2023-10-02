package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.CreateUnOfficialGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.CreateUnOfficialGroupResponseDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.SearchGroupDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupRestController {

    private final GroupService groupService;

    /*
        공개, 비공개 그룹 생성
        - RequestDTO에서 groupType을 받아서 type 확인을 하고 if문으로 나누는게 좋을까
        - /create/closed, /create/opened 따로 받는게 좋을까
        - 그룹 생성 시 기본 페이지 생성은 어떻게 하는게 좋을까
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUnOfficialGroup(
            @RequestBody @Valid CreateUnOfficialGroupRequestDTO requestDTO, Errors errors) {

        // TODO: JWT Token에서 memberId 획득
        Long tempMemberId = 1L;

        CreateUnOfficialGroupResponseDTO response = groupService.createUnOfficialGroup(requestDTO, tempMemberId);

        return ResponseEntity.ok().body(ApiUtils.success(response));
    }
    
    /*
        그룹 검색
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchGroup(@RequestParam(value = "keyword", required = false) String keyword) {
        
        // TODO: 페이지네이션 필요
        SearchGroupDTO response = groupService.searchGroupByKeyword(keyword);

        return ResponseEntity.ok().body(ApiUtils.success(response));
    }
    
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
