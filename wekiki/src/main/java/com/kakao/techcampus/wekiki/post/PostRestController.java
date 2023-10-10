package com.kakao.techcampus.wekiki.post;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody PostRequest.createPostDTO request) {

        Long tempUserId = 1L;

        PostResponse.createPostDTO response = postService.createPost(tempUserId,request.getPageId(), request.getParentPostId(),
                request.getOrder(), request.getTitle(),request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyPost(@RequestBody PostRequest.modifyPostDTO request){

        Long tempUserId = 1L;

        PostResponse.modifyPostDTO response = postService.modifyPost(tempUserId, request.getPostId(), request.getTitle(), request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @DeleteMapping("/{postid}")
    public ResponseEntity<?> deletePost(@PathVariable Long postid){

        Long tempUserId = 1L;

        PostResponse.deletePostDTO response = postService.deletePost(tempUserId, postid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }


}
