package com.kakao.techcampus.wekiki.comment;

import com.kakao.techcampus.wekiki._core.errors.ApplicationException;
import com.kakao.techcampus.wekiki._core.errors.ErrorCode;
import com.kakao.techcampus.wekiki.post.Post;
import com.kakao.techcampus.wekiki.post.PostJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentJPARepository commentJPARepository;
    private final PostJPARepository postJPARepository;

    final int COMMENT_COUNT = 10;

    @Transactional
    public CommentResponse.getCommentDTO getComment(Long userId, Long groupId, Long postId, int pageNo){

        // 1. userId로 user 객체 들고오기

        // 2. groupMember 맞는지 확인하기

        // 3. post 존재하는지 예외처리
        Post post = postJPARepository.findById(postId).orElseThrow(
                () -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        // 4. postId로 Comment 다 가져오기
        Pageable pageable = PageRequest.of(pageNo, COMMENT_COUNT);
        Page<Comment> comments = commentJPARepository.findCommentsByPostId(postId, pageable);

        // 5. return DTO
        List<CommentResponse.getCommentDTO.commentDTO> commentDTOs = comments.getContent().stream().map(c -> new CommentResponse.getCommentDTO.commentDTO(c))
                .collect(Collectors.toList());
        return new CommentResponse.getCommentDTO(post,commentDTOs);
    }

    @Transactional
    public CommentResponse.createCommentDTO createComment(Long userId, Long groupId, Long postId, String content){

        // 1. userId로 user 객체 들고오기

        // 2. groupMember 맞는지 확인하기

        // 3. post 존재하는지 예외처리
        Post post = postJPARepository.findById(postId).orElseThrow(
                () -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        // 4. comment 생성
        Comment comment = Comment.builder()
                //.member()
                .post(post)
                .content(content)
                .created_at(LocalDateTime.now())
                .build();
        Comment savedComment = commentJPARepository.save(comment);

        // 5. return DTO
        return new CommentResponse.createCommentDTO(savedComment,"temp");
    }



}
