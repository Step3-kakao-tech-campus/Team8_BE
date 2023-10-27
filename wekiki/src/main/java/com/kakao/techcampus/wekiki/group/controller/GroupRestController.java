package com.kakao.techcampus.wekiki.group.controller;

import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import com.kakao.techcampus.wekiki.group.dto.requestDTO.CreateUnOfficialGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.dto.requestDTO.JoinGroupRequestDTO;
import com.kakao.techcampus.wekiki.group.dto.requestDTO.UpdateMyGroupPageDTO;
import com.kakao.techcampus.wekiki.group.dto.responseDTO.*;
import com.kakao.techcampus.wekiki.group.service.GroupService;
import com.kakao.techcampus.wekiki.group.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import static com.kakao.techcampus.wekiki._core.utils.SecurityUtils.currentMember;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupRestController {

    private final GroupService groupService;
    private final InvitationService invitationService;

    /*
        공개, 비공개 그룹 생성
        - RequestDTO에서 groupType을 받아서 type 확인을 하고 if문으로 나누는게 좋을까
        - /create/closed, /create/opened 따로 받는게 좋을까
        - 그룹 생성 시 기본 페이지 생성은 어떻게 하는게 좋을까
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUnOfficialGroup(
            @RequestBody @Valid CreateUnOfficialGroupRequestDTO requestDTO, Errors errors) {

        CreateUnOfficialGroupResponseDTO response = groupService.createUnOfficialGroup(requestDTO, currentMember());

        return ResponseEntity.ok().body(ApiUtils.success(response));
    }
    
    /*
        그룹 검색
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchGroup(@RequestParam(value = "keyword", required = false) String keyword) {

        SearchGroupDTO responseDTO = groupService.searchGroupByKeyword(keyword);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    /*
        그룹 추가 검색 - 공식 그룹
     */
    @GetMapping("/search/officialGroup")
    public ResponseEntity<?> searchOfficialGroup(@RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {

        SearchOfficialGroupResponseDTO responseDTO = groupService.searchOfficialGroupByKeyword(keyword, page, size);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    /*
        그룹 추가 검색 - 비공식 공개 그룹
     */
    @GetMapping("/search/unOfficialGroup")
    public ResponseEntity<?> searchUnOfficialGroup(@RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {

        SearchUnOfficialGroupResponseDTO responseDTO = groupService.searchUnOfficialGroupByKeyword(keyword, page, size);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
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


        groupService.joinGroup(groupId, currentMember(), requestDTO);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    /*
        그룹 초대링크 확인
     */
    @GetMapping("/{groupId}/invitationLink")
    public ResponseEntity<?> getInvitationLink(@PathVariable("groupId") Long groupId) {

        GetInvitationLinkResponseDTO responseDTO = invitationService.getGroupInvitationCode(groupId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    /*
        그룹 초대링크 유효성 검사(?)
     */
    @GetMapping("/{invitationLink}")
    public ResponseEntity<?> ValidateInvitation(@PathVariable String invitationLink) {

        ValidateInvitationResponseDTO responseDTO = invitationService.ValidateInvitation(invitationLink);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
    
    /*
        그룹 내 본인 정보 조회
        - 그룹 이름
        - 그룹 닉네임 수정칸
        - 내 문서 기여 목록
     */
    @GetMapping("/{groupId}/myInfo")
    public ResponseEntity<?> myGroupPage(@PathVariable("groupId") Long groupId) {

        MyGroupInfoResponseDTO responseDTO = groupService.getMyGroupInfo(groupId, currentMember());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
    
    /*
        그룹 내 본인 정보 수정
     */
    @PatchMapping("/{groupId}/myInfo")
    public ResponseEntity<?> updateMyGroupPage(@PathVariable("groupId") Long groupId, UpdateMyGroupPageDTO requestDTO) {

        groupService.updateMyGroupPage(groupId, currentMember(), requestDTO);
        
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    /*
        내 문서 기여 목록
     */
    @GetMapping("/{groupId}/myInfo/myHistory")
    public ResponseEntity<?> myGroupHistoryPage(@PathVariable("groupId") Long groupId,
                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        MyGroupHistoryResponseDTO responseDTO = groupService.getMyGroupHistory(groupId, currentMember(), page, size);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
    
    /*
        그룹 탈퇴
     */
    @DeleteMapping("/{groupId}/myInfo")
    public ResponseEntity<?> leaveGroup(@PathVariable("groupId") Long groupId) {

        groupService.leaveGroup(groupId, currentMember());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}
