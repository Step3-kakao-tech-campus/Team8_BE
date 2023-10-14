package com.kakao.techcampus.wekiki.comment;

import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.errors.ApplicationException;
import com.kakao.techcampus.wekiki._core.errors.ErrorCode;
import com.kakao.techcampus.wekiki.group.Group;
import com.kakao.techcampus.wekiki.group.GroupJPARepository;
import com.kakao.techcampus.wekiki.group.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import com.kakao.techcampus.wekiki.page.PageInfo;
import com.kakao.techcampus.wekiki.page.PageJPARepository;
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
    private final MemberJPARepository memberJPARepository;
    private final GroupMemberJPARepository groupMemberJPARepository;
    private final GroupJPARepository groupJPARepository;
    final int COMMENT_COUNT = 10;

    @Transactional
    public CommentResponse.getCommentDTO getComment(Long memberId, Long groupId, Long postId, int pageNo){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        ActiveGroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. post 존재하는지 예외처리
        Post post = checkPostFromPostId(postId);

        // 5. postId로 Comment 다 가져오기
        Pageable pageable = PageRequest.of(pageNo, COMMENT_COUNT);
        Page<Comment> comments = commentJPARepository.findCommentsByPostId(postId, pageable); // TODO : fetch join (comment랑 groupmember)

        // 6. return DTO
        List<CommentResponse.getCommentDTO.commentDTO> commentDTOs = comments.getContent()
                .stream().map(c -> new CommentResponse.getCommentDTO.commentDTO(c , c.getGroupMember()))
                .collect(Collectors.toList());
        return new CommentResponse.getCommentDTO(post,commentDTOs);
    }

    @Transactional
    public CommentResponse.createCommentDTO createComment(Long memberId, Long groupId, Long postId, String content){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        ActiveGroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. post 존재하는지 예외처리
        Post post = checkPostFromPostId(postId);

        // 4. comment 생성
        Comment comment = Comment.builder()
                .groupMember(groupMember)
                .post(post)
                .content(content)
                .created_at(LocalDateTime.now())
                .build();
        Comment savedComment = commentJPARepository.save(comment);

        // 5. return DTO
        return new CommentResponse.createCommentDTO(savedComment,groupMember.getNickName());
    }

    @Transactional
    public CommentResponse.deleteCommentDTO deleteComment(Long memberId, Long groupId, Long commentId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        ActiveGroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. comment 존재하는지 예외처리
        Comment comment = checkCommentFromCommentId(commentId);

        // 5. comment 쓴 사람이 삭제하는 유저랑 일치하는지 확인
        if(comment.getGroupMember().getId() != groupMember.getId()){
            throw new ApplicationException(ErrorCode.COMMENT_MEMBER_INCONSISTENCY);
        }

        // 5. comment 삭제
        CommentResponse.deleteCommentDTO response = new CommentResponse.deleteCommentDTO(comment);
        commentJPARepository.delete(comment);

        // 6. return DTO
        return response;
    }

    @Transactional
    public CommentResponse.updateCommentDTO updateComment(Long memberId, Long groupId, Long commentId, String updateContent){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        ActiveGroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. comment 존재하는지 예외처리
        Comment comment = checkCommentFromCommentId(commentId);

        // 5. comment 쓴 사람이 삭제하는 유저랑 일치하는지 확인
        if(comment.getGroupMember().getId() != groupMember.getId()){
            throw new ApplicationException(ErrorCode.COMMENT_MEMBER_INCONSISTENCY);
        }

        // 6. 내용 동일하면 exception
        if(comment.getContent().equals(updateContent)){
            throw new ApplicationException(ErrorCode.COMMENT_SAME_DATE);
        }

        // 7. 수정
        comment.updateContent(updateContent);

        // 8. return DTO
        return new CommentResponse.updateCommentDTO(comment);
    }


    public Member checkMemberFromMemberId(Long memberId){
        return memberJPARepository.findById(memberId)
                .orElseThrow(()-> new Exception404("존재하지 않는 회원입니다."));
    }

    public Group checkGroupFromGroupId(Long groupId){
        return groupJPARepository.findById(groupId)
                .orElseThrow(()-> new Exception404("존재하지 않는 그룹입니다."));
    }

    public ActiveGroupMember checkGroupMember(Long memberId, Long groupId){
        return groupMemberJPARepository.findGroupMemberByMemberIdAndGroupId(memberId,groupId)
                .orElseThrow(() -> new Exception404("해당 그룹에 속한 회원이 아닙니다."));
    }

    public Post checkPostFromPostId(Long postId){
        return postJPARepository.findById(postId)
                .orElseThrow(() -> new Exception404("존재하지 않는 글 입니다."));
    }

    public Comment checkCommentFromCommentId(Long commentId){
        return commentJPARepository.findById(commentId)
                .orElseThrow(() -> new Exception404("존재하지 않는 댓글 입니다."));
    }

}
