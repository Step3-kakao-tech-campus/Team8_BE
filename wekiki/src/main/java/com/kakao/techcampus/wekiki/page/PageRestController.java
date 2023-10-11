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
     페이지 + 글 조회 기능

     */

    @GetMapping("/page/{pageid}")
    public void getPageFromId(@PathVariable Long pageid) {


    }

    /*
     페이지 목차 조회
     */

    @GetMapping("/page/index/{pageid}")
    public void getIndex(@PathVariable Long pageid) {


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
    public void deletePage(@PathVariable Long pageid) {


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
      페이지 제목으로 페이지 조회

     */
    @GetMapping("/group/{groupid}/page")
    public void getPageFromTitle(@PathVariable Long groupid,@RequestParam(value = "title") String title) {


    }


    /*
     페이지 키워드 검색 기능

     */

    @GetMapping("/group/{groupid}/page/search")
    public ResponseEntity<?> searchPage(@PathVariable Long groupid,@RequestParam(value = "keyword" , defaultValue = "") String keyword, @RequestParam(value = "page", defaultValue = "1") int page) {

        List<PageInfoResponse.searchPageDTO> response = pageService.searchPage(page-1, keyword);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     최근 바뀐 페이지 목차 조회 기능


     */
    @GetMapping("/group/{groupid}/page/recent")
    public void getRecentPage(@PathVariable Long groupid){

    }

    /*
     페이지 링크 걸기


     */
    @GetMapping("/group/{groupid}/page/link")
    public void getPageLink(@PathVariable Long groupid, @RequestParam(value = "title") String title){

    }


}
