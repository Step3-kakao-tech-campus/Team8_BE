package com.kakao.techcampus.wekiki.post;


import com.kakao.techcampus.wekiki._core.utils.ApiUtils;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import com.kakao.techcampus.wekiki.report.Report;
import com.kakao.techcampus.wekiki.report.ReportJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kakao.techcampus.wekiki._core.utils.SecurityUtils.currentMember;

@RestController
@RequestMapping("/group/{groupid}/post")
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@PathVariable Long groupid, @RequestBody PostRequest.createPostDTO request) {

        PostResponse.createPostDTO response = postService.createPost(currentMember(),groupid,request.getPageId(), request.getParentPostId(),
                request.getOrder(), request.getTitle(),request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modifyPost(@PathVariable Long groupid,@RequestBody PostRequest.modifyPostDTO request){

        PostResponse.modifyPostDTO response = postService.modifyPost(currentMember(),groupid, request.getPostId(), request.getTitle(), request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @DeleteMapping("/{postid}")
    public ResponseEntity<?> deletePost(@PathVariable Long groupid, @PathVariable Long postid){

        PostResponse.deletePostDTO response = postService.deletePost(currentMember(), groupid, postid);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @GetMapping("/{postid}/history")
    public ResponseEntity<?> getPostHistory(@PathVariable Long groupid,@PathVariable Long postid
            ,@RequestParam(value = "page", defaultValue = "1") int page){

        PostResponse.getPostHistoryDTO response = postService.getPostHistory(currentMember(), groupid, postid, page - 1);

        return ResponseEntity.ok(ApiUtils.success(response));
    }

    @PostMapping("/report")
    public ResponseEntity<?> createReport(@PathVariable Long groupid , @RequestBody PostRequest.createReportDTO request){

        PostResponse.createReportDTO response = postService.createReport(currentMember(), groupid, request.getPostId(), request.getContent());

        return ResponseEntity.ok(ApiUtils.success(response));
    }




}
