package com.kakao.techcampus.wekiki.page;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page")
@RequiredArgsConstructor
public class PageRestController {

    //private final PageService pageService;

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
    public void createPage() {


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
    public void likePage(@PathVariable Long pageid) {


    }

    /*
     페이지 싫어요 기능

     */

    @PostMapping("/{pageid}/hate")
    public void hatePage(@PathVariable Long pageid) {


    }

    /*
     페이지 키워드 검색 기능

     */

    @GetMapping("/search")
    public void searchPage(@RequestParam("keyword") String keyword) {


    }
}