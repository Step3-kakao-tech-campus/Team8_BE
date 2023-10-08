package com.kakao.techcampus.wekiki.post;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody PostRequest.createPostDTO request) {

        System.out.println(request.toString());

        PostResponse.createPostDTO response = postService.createPost(request.getPageId(), request.getParentPostId(),
                request.getOrder(), request.getTitle(),request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }


}
