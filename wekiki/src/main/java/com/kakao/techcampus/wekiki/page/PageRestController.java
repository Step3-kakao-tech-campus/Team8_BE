package com.kakao.techcampus.wekiki.page;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kakao.techcampus.wekiki._core.utils.SecurityUtils.currentMember;

@RestController
@RequestMapping("/group/{groupid}/page")
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;

    /*
     페이지 ID로 페이지 + 글 조회 기능

     */



    @GetMapping("/{pageid}")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.getPageFromIdDTO>> getPageFromId(@PathVariable Long groupid, @PathVariable Long pageid) {

        PageInfoResponse.getPageFromIdDTO response = pageService.getPageFromId(currentMember(), groupid, pageid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }


    /*
     페이지 생성 기능

     */

    @PostMapping("/create")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.createPageDTO>> createPage(@PathVariable Long groupid, @RequestBody PageInfoRequest.createPageDTO request) {

        PageInfoResponse.createPageDTO response = pageService.createPage(request.getPageName(), groupid, currentMember());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 삭제 기능

     */

    @DeleteMapping("/{pageid}")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.deletePageDTO>> deletePage(@PathVariable Long groupid, @PathVariable Long pageid) {

        PageInfoResponse.deletePageDTO response = pageService.deletePage(currentMember(),groupid, pageid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 좋아요 기능

     */

    @PostMapping("/{pageid}/like")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.likePageDTO>> likePage(@PathVariable Long groupid, @PathVariable Long pageid) {

        PageInfoResponse.likePageDTO response = pageService.likePage(pageid, groupid, currentMember());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 싫어요 기능

     */

    @PostMapping("/{pageid}/hate")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.hatePageDTO>> hatePage(@PathVariable Long groupid, @PathVariable Long pageid) {

        PageInfoResponse.hatePageDTO response = pageService.hatePage(pageid, groupid, currentMember());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
    페이지 목차 조회 기능

     */

    @GetMapping("/{pageid}/index")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.getPageIndexDTO>> getPageIndex(@PathVariable Long groupid, @PathVariable Long pageid) {

        PageInfoResponse.getPageIndexDTO response = pageService.getPageIndex(groupid,currentMember(), pageid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }



    // ========================================================================

    /*
      페이지 제목으로 페이지 조회

     */
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.getPageFromIdDTO>> getPageFromTitle(@PathVariable Long groupid, @RequestParam(value = "title") String title) {

        PageInfoResponse.getPageFromIdDTO response = pageService.getPageFromTitle(currentMember(), groupid,title);

        return ResponseEntity.ok(ApiUtils.success(response));

    }

    /*
     페이지 키워드 검색 기능

     */

    @GetMapping("/search")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.searchPageDTO>> searchPage(@PathVariable Long groupid,
                                                                                         @RequestParam(value = "keyword" , defaultValue = "") String keyword,
                                                                                         @RequestParam(value = "page", defaultValue = "1") int page) {

        PageInfoResponse.searchPageDTO response = pageService.searchPage(groupid, currentMember(),page - 1, keyword);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     최근 바뀐 페이지 목차 조회 기능


     */
    @GetMapping("/recent")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.getRecentPageDTO>> getRecentPage(@PathVariable Long groupid){

        PageInfoResponse.getRecentPageDTO response = pageService.getRecentPage(currentMember(), groupid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 링크 걸기


     */
    @GetMapping("/link")
    public ResponseEntity<ApiUtils.ApiResult<PageInfoResponse.getPageLinkDTO>> getPageLink(@PathVariable Long groupid, @RequestParam(value = "title") String title){

        PageInfoResponse.getPageLinkDTO response = pageService.getPageLink(currentMember(), groupid, title);

        return ResponseEntity.ok(ApiUtils.success(response));
    }
}
