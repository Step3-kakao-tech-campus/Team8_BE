package com.kakao.techcampus.wekiki.comment;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/{groupid}/post/{postid}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getComment(@PathVariable Long groupid,
                                        @PathVariable Long postid,
                                        @RequestParam(value = "page", defaultValue = "1") int page){

        Long tempUserId = 1L;

        CommentResponse.getCommentDTO response = commentService.getComment(tempUserId, groupid, postid, page-1);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable Long groupid,
                                           @PathVariable Long postid,
                                           @RequestBody CommentRequest.createComment request){

        Long tempUserId = 1L;

        CommentResponse.createCommentDTO response = commentService.createComment(tempUserId, groupid, postid, request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }



}
