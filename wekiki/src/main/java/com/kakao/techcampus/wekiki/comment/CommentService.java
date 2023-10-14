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

    @Transactional
    public CommentResponse.deleteCommentDTO deleteComment(Long memberId, Long groupId, Long commentId){

        // 1. userId로 user 객체 들고오기

        // 2. groupMember 맞는지 확인하기

        // 3. comment 존재하는지 예외처리
        Comment comment = commentJPARepository.findById(commentId).orElseThrow(
                () -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        // 4. comment 쓴 사람이 삭제하는 유저랑 일치하는지 확인
//        if(comment.getMember().getId() != memberId){
//            throw new ApplicationException(ErrorCode.COMMENT_MEMBER_INCONSISTENCY);
//        }

        // 5. comment 삭제
        CommentResponse.deleteCommentDTO response = new CommentResponse.deleteCommentDTO(comment);
        commentJPARepository.delete(comment);

        // 6. return DTO
        return response;
    }

    @Transactional
    public CommentResponse.updateCommentDTO updateComment(Long memberId, Long groupId, Long commentId, String updateContent){

        // 1. userId로 user 객체 들고오기

        // 2. groupMember 맞는지 확인하기

        // 3. comment 존재하는지 예외처리
        Comment comment = commentJPARepository.findById(commentId).orElseThrow(
                () -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND));

        // 4. comment 쓴 사람이 수정하는 유저랑 일치하는지 확인
//        if(comment.getMember().getId() != memberId){
//            throw new ApplicationException(ErrorCode.COMMENT_MEMBER_INCONSISTENCY);
//        }

        // 5. 내용 동일하면 exception
        if(comment.getContent().equals(updateContent)){
            throw new ApplicationException(ErrorCode.COMMENT_SAME_DATE);
        }

        // 6. 수정
        comment.updateContent(updateContent);

        // 7. return DTO
        return new CommentResponse.updateCommentDTO(comment);
    }



}
