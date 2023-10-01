package com.kakao.techcampus.wekiki.page;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page")
@RequiredArgsConstructor
public class PageRestController {

    private final PageService pageService;

    /*
     페이지 + 글 조회 기능

     */

    @GetMapping("/{pageid}")
    public void getPage(@PathVariable Long pageid) {


    }

    /*
     페이지 생성 기능

     */

    @PostMapping("/create")
    public ResponseEntity<?> createPage(@RequestBody PageRequest.createPageDTO request) {

        // TODO : JWT에서 userId 꺼내도록 수정
        Long tempUserId = 1L;

        PageResponse.createPageDTO response = pageService.createPage(request.getTitle(), request.getGroupId(), tempUserId);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 삭제 기능

     */

    @DeleteMapping("/{pageid}")
    public void deletePage(@PathVariable Long pageid) {


    }

    /*
     페이지 좋아요 기능

     */

    @PostMapping("/{pageid}/like")
    public ResponseEntity<?> likePage(@PathVariable Long pageid , @RequestBody PageRequest.likePageDTO request) {

        // TODO : JWT에서 userId 꺼내도록 수정
        Long tempUserId = 1L;

        PageResponse.likePageDTO response = pageService.likePage(pageid, request.getGroupId(), tempUserId);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    /*
     페이지 싫어요 기능

     */

    @PostMapping("/{pageid}/hate")
    public ResponseEntity<?> hatePage(@PathVariable Long pageid , @RequestBody PageRequest.hatePageDTO request) {

        // TODO : JWT에서 userId 꺼내도록 수정
        Long tempUserId = 1L;

        PageResponse.hatePageDTO response = pageService.hatePage(pageid, request.getGroupId(), tempUserId);

        return ResponseEntity.ok(ApiUtils.success(response));

    }

    /*
     페이지 키워드 검색 기능

     */

    @GetMapping("/search")
    public void searchPage(@RequestParam("keyword") String keyword) {


    }
}
