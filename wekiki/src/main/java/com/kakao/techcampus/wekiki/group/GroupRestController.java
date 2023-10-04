package com.kakao.techcampus.wekiki.group;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.CreateUnOfficialGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.JoinGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.requestDTO.UpdateMyGroupPageDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.CreateUnOfficialGroupResponseDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.MyGroupInfoResponseDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.SearchGroupDTO;
import com.kakao.techcampus.wekiki.group.groupDTO.responseDTO.SearchGroupInfoDTO;
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
    @GetMapping("/search/{grouId}")
    public ResponseEntity<?> searchGroupInfo(@PathVariable("groupId") Long groupId) {
        SearchGroupInfoDTO response = groupService.getGroupInfo(groupId);

        return ResponseEntity.ok().body(ApiUtils.success(response));
    }
    
    /*
        비공식 공개 그룹 입장 비밀번호 확인
        - 그 후 그룹 참가로 이동
     */
    @PostMapping("/{groupId}/entry")
    public ResponseEntity<?> groupEntry(@PathVariable("groupId") Long groupId, @RequestParam("entrancePassword") String entrancePassword) {

        groupService.groupEntry(groupId, entrancePassword);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    /*
        그룹 참가
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinGroup(@PathVariable("groupId") Long groupId, JoinGroupRequestDTO requestDTO) {

        // TODO: JWT Token에서 memberId 획득
        Long tempMemberId = 1L;

        groupService.joinGroup(groupId, tempMemberId, requestDTO);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
    
    /*
        그룹 초대
     */
    
    /*
        그룹 내 본인 정보 조회
        - 그룹 이름
        - 그룹 닉네임 수정칸
        - 내 문서 기여 목록
     */
    @GetMapping("/{groupId}/myInfo")
    public ResponseEntity<?> myGroupPage(@PathVariable("groupId") Long groupId) {

        // TODO: JWT Token에서 memberId 획득
        Long tempMemberId = 1L;

        MyGroupInfoResponseDTO responseDTO = groupService.getMyGroupInfo(groupId, tempMemberId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
    
    /*
        그룹 내 본인 정보 수정
     */
    @PatchMapping("/{groupId}/myInfo")
    public ResponseEntity<?> updateMyGroupPage(@PathVariable("groupId") Long groupId, UpdateMyGroupPageDTO requestDTO) {

        // TODO: JWT Token에서 memberId 획득
        Long tempMemberId = 1L;

        groupService.updateMyGroupPage(groupId, tempMemberId, requestDTO);
        
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
    
    /*
        그룹 탈퇴
     */

}
