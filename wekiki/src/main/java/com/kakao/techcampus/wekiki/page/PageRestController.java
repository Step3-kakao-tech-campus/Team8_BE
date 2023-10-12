package com.kakao.techcampus.wekiki.page;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;

    /*
     페이지 ID로 페이지 + 글 조회 기능

     */

    @GetMapping("/page/{pageid}")
    public ResponseEntity<?> getPageFromId(@PathVariable Long pageid) {

        Long tempUserId = 1L;

        PageInfoResponse.getPageFromIdDTO response = pageService.getPageFromId(tempUserId, pageid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }


    /*
     페이지 생성 기능

     */

    @PostMapping("/page/create")
    public ResponseEntity<?> createPage(@RequestBody PageInfoRequest.createPageDTO request) {

        // TODO : JWT에서 userId 꺼내도록 수정
        Long tempUserId = 1L;

        PageInfoResponse.createPageDTO response = pageService.createPage(request.getTitle(), request.getGroupId(), tempUserId);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 삭제 기능

     */

    @DeleteMapping("/page/{pageid}")
    public ResponseEntity<?> deletePage(@PathVariable Long pageid) {

        Long tempUserId = 1L;

        PageInfoResponse.deletePageDTO response = pageService.deletePage(tempUserId, pageid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 좋아요 기능

     */

    @PostMapping("/page/{pageid}/like")
    public ResponseEntity<?> likePage(@PathVariable Long pageid) {

        // TODO : JWT에서 userId 꺼내도록 수정
        Long tempUserId = 1L;

        PageInfoResponse.likePageDTO response = pageService.likePage(pageid, tempUserId);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 싫어요 기능

     */

    @PostMapping("/page/{pageid}/hate")
    public ResponseEntity<?> hatePage(@PathVariable Long pageid) {

        // TODO : JWT에서 userId 꺼내도록 수정
        Long tempUserId = 1L;

        PageInfoResponse.hatePageDTO response = pageService.hatePage(pageid, tempUserId);

        return ResponseEntity.ok(ApiUtils.success(response));

    }

    /*
    페이지 목차 조회 기능

     */

    @GetMapping("/page/{pageid}/index")
    public ResponseEntity<?> getPageIndex(@PathVariable Long pageid) {

        Long tempUserId = 1L;

        PageInfoResponse.getPageIndexDTO response = pageService.getPageIndex(tempUserId, pageid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }



    // ========================================================================

    /*
      페이지 제목으로 페이지 조회

     */
    @GetMapping("/group/{groupid}/page")
    public ResponseEntity<?> getPageFromTitle(@PathVariable Long groupid,@RequestParam(value = "title") String title) {

        Long tempUserId = 1L;

        PageInfoResponse.getPageFromIdDTO response = pageService.getPageFromTitle(tempUserId, groupid,title);

        return ResponseEntity.ok(ApiUtils.success(response));

    }

    /*
     페이지 키워드 검색 기능

     */

    @GetMapping("/group/{groupid}/page/search")
    public ResponseEntity<?> searchPage(@PathVariable Long groupid,@RequestParam(value = "keyword" , defaultValue = "") String keyword, @RequestParam(value = "page", defaultValue = "1") int page) {

        Long tempUserId = 1L;

        List<PageInfoResponse.searchPageDTO> response = pageService.searchPage(page-1, keyword);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     최근 바뀐 페이지 목차 조회 기능


     */
    @GetMapping("/group/{groupid}/page/recent")
    public ResponseEntity<?> getRecentPage(@PathVariable Long groupid){

        Long tempUserId = 1L;

        PageInfoResponse.getRecentPageDTO response = pageService.getRecentPage(tempUserId, groupid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 링크 걸기


     */
    @GetMapping("/group/{groupid}/page/link")
    public ResponseEntity<?> getPageLink(@PathVariable Long groupid, @RequestParam(value = "title") String title){

        Long tempUserId = 1L;

        PageInfoResponse.getPageLinkDTO response = pageService.getPageLink(tempUserId, groupid, title);

        return ResponseEntity.ok(ApiUtils.success(response));
    }


}
