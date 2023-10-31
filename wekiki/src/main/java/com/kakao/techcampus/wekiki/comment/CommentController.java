package com.kakao.techcampus.wekiki.comment;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kakao.techcampus.wekiki._core.utils.SecurityUtils.currentMember;

@RestController
@RequestMapping("/group/{groupid}")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{postid}/comment")
    public ResponseEntity<ApiUtils.ApiResult<CommentResponse.getCommentDTO>> getComment(@PathVariable Long groupid,
                                                                                        @PathVariable Long postid,
                                                                                        @RequestParam(value = "page", defaultValue = "1") int page){

        CommentResponse.getCommentDTO response = commentService.getComment(currentMember(), groupid, postid, page-1);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @PostMapping("/post/{postid}/comment")
    public ResponseEntity<ApiUtils.ApiResult<CommentResponse.createCommentDTO>> createComment(@PathVariable Long groupid,
                                                                                              @PathVariable Long postid,
                                                                                              @RequestBody CommentRequest.createComment request){

        CommentResponse.createCommentDTO response = commentService.createComment(currentMember(), groupid, postid, request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @DeleteMapping("/comment/{commentid}")
    public ResponseEntity<ApiUtils.ApiResult<CommentResponse.deleteCommentDTO>> deleteComment(@PathVariable Long groupid,
                                                                                              @PathVariable Long commentid){

        CommentResponse.deleteCommentDTO response = commentService.deleteComment(currentMember(), groupid, commentid);

        return ResponseEntity.ok(ApiUtils.success(response));

    }

    @PatchMapping("/comment/{commentid}")
    public ResponseEntity<ApiUtils.ApiResult<CommentResponse.updateCommentDTO>> updateComment(@PathVariable Long groupid,
                                                                                              @PathVariable Long commentid,
                                                                                              @RequestBody CommentRequest.updateComment request){

        CommentResponse.updateCommentDTO response = commentService.updateComment(currentMember(), groupid, commentid, request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));

    }



}
