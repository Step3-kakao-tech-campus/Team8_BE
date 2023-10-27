package com.kakao.techcampus.wekiki.post;


import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.repository.GroupJPARepository;
import com.kakao.techcampus.wekiki.group.domain.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.group.repository.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import com.kakao.techcampus.wekiki.page.PageInfo;
import com.kakao.techcampus.wekiki.page.PageJPARepository;
import com.kakao.techcampus.wekiki.report.Report;
import com.kakao.techcampus.wekiki.report.ReportJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PageJPARepository pageJPARepository;
    private final PostJPARepository postJPARepository;
    private final HistoryJPARepository historyJPARepository;
    private final MemberJPARepository memberJPARepository;
    private final GroupMemberJPARepository groupMemberJPARepository;
    private final GroupJPARepository groupJPARepository;
    private final ReportJPARepository reportJPARepository;
    final int HISTORY_COUNT = 5;

    @Transactional
    public PostResponse.createPostDTO createPost(Long memberId,Long groupId,Long pageId, Long parentPostId, int order, String title, String content){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        ActiveGroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. pageId로 PageInfo 객체 들고오기
        PageInfo pageInfo = checkPageFromPageId(pageId);
        pageInfo.updatePage();

        // 5. parentPostId로 parentPost 가져오기
        Post parent = null;
        if(parentPostId != 0) {
            parent = postJPARepository.findById(parentPostId).orElseThrow(
                    () -> new Exception404("존재하지 않는 상위 글입니다."));
        }

        // 6. 같은 pageId를 가진 Post들 중에 입력받은 order보다 높은 모든 Post들의 order를 1씩 증가
        postJPARepository.findPostsByPageIdAndOrderGreaterThan(pageId, order).stream().forEach(p -> p.plusOrder());

        // 7. Post 엔티티 생성하고 저장하기
        Post newPost = Post.builder()
                .parent(parent)
                .orders(order)
                .groupMember(groupMember)
                .pageInfo(pageInfo)
                .title(title)
                .content(content)
                .created_at(LocalDateTime.now())
                .build();
        Post savedPost = postJPARepository.save(newPost);

        // 8. 히스토리 생성
        History newHistory = History.builder()
                .post(savedPost)
                .build();
        historyJPARepository.save(newHistory);

        // 9. return DTO
        return new PostResponse.createPostDTO(savedPost);
    }

    @Transactional
    public PostResponse.modifyPostDTO modifyPost(Long memberId , Long groupId, Long postId , String title, String content){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        ActiveGroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. postId로 post 엔티티 가져오기
        Post post = checkPostFromPostId(postId);

        // 5. 현재 Post랑 내용 같은지 확인
        if(post.getTitle().equals(title) && post.getContent().equals(content)){
            throw new Exception400("기존 글과 동일한 글입니다.");
        }

        // 6. 다르면 Post 수정후 히스토리 생성 저장
        post.modifyPost(groupMember,title,content);

        History newHistory = History.builder()
                .post(post)
                .build();
        historyJPARepository.save(newHistory);

        // 7. return DTO
        return new PostResponse.modifyPostDTO(post);
    }

    @Transactional
    public PostResponse.getPostHistoryDTO getPostHistory(Long memberId, Long groupId, Long postId , int pageNo){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. postId로 post 엔티티 가져오기
        Post post = checkPostFromPostId(postId);

        // 5. 해당 PostId로 history 모두 가져오기 시간순 + 페이지네이션 (memberId, nickName, historyId,title, content, created_at)
        PageRequest pageRequest = PageRequest.of(pageNo, HISTORY_COUNT);
        Page<History> historys = historyJPARepository.findHistoryWithMemberByPostId(postId, pageRequest);
        //Page<History> historys = historyJPARepository.findHistoryByPostId(postId, pageRequest);

        // 6. DTO로 return
        List<PostResponse.getPostHistoryDTO.historyDTO> historyDTOs = historys.getContent().stream().
                map(h -> new PostResponse.getPostHistoryDTO.historyDTO(h.getGroupMember(),h)).collect(Collectors.toList());
        return new PostResponse.getPostHistoryDTO(post,historyDTOs);

    }

    @Transactional
    public PostResponse.deletePostDTO deletePost(Long memberId, Long groupId, Long postId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. postId로 post 엔티티 가져오기
        Post post = checkPostFromPostId(postId);

        // 5. parent로 해당 postId를 가지고 있는 post가 있는지 확인 -> 존재하면 Exception
        if(postJPARepository.existsByParentId(postId)){
            throw new Exception400("하위 글이 존재하는 글은 삭제가 불가능합니다.");
        }

        // 6. child post 존재 안하면 history + post 삭제 시키기
        PostResponse.deletePostDTO response = new PostResponse.deletePostDTO(post);
        historyJPARepository.deleteByPostId(postId);
        postJPARepository.deleteById(postId);

        // 7. order값 앞으로 땡기기
        postJPARepository.findPostsByPageIdAndOrderGreaterThan(post.getPageInfo().getId(), post.getOrders());

        // 8. return DTO;
        return response;

    }

    @Transactional
    public PostResponse.createReportDTO createReport(Long memberId, Long groupId, Long postId , String content){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        GroupMember groupMember = checkGroupMember(memberId, groupId);

        // 4. postId로 post 엔티티 가져오기
        checkPostFromPostId(postId);

        // 5. postId의 최근 히스토리 가져오기
        List<History> historyByPostId = historyJPARepository.findHistoryByPostId(postId, PageRequest.of(0, 1));

        // 6. report 생성
        Report report = Report.builder()
                .groupMember(groupMember)
                .history(historyByPostId.get(0))
                .content(content)
                .created_at(LocalDateTime.now())
                .build();
        Report savedReport = reportJPARepository.save(report);

        // 7. return DTO
        return new PostResponse.createReportDTO(savedReport);

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

    public PageInfo checkPageFromPageId(Long pageId){
        return pageJPARepository.findById(pageId)
                .orElseThrow(() -> new Exception404("존재하지 않는 페이지 입니다."));
    }

    public Post checkPostFromPostId(Long postId){
        return postJPARepository.findById(postId)
                .orElseThrow(() -> new Exception404("존재하지 않는 글 입니다."));
    }

}
