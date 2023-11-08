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
    private final GroupMemberJPARepository groupMemberJPARepository;
    private final ReportJPARepository reportJPARepository;
    final int HISTORY_COUNT = 5;

    @Transactional
    public PostResponse.createPostDTO createPost(Long memberId,Long groupId,Long pageId, Long parentPostId, int order, String title, String content){

        // 1. 존재하는 Member, Group, GroupMember 인지 fetch join으로 하나의 쿼리로 확인
        ActiveGroupMember activeGroupMember = checkGroupMember(memberId, groupId);

        // 2. pageId로 PageInfo 객체 들고오기
        PageInfo pageInfo = checkPageFromPageId(pageId);
        pageInfo.updatePage();

        // 3. parentPostId로 parentPost 가져오기
        Post parent = null;
        if(parentPostId != 0) {
            parent = postJPARepository.findById(parentPostId).orElseThrow(
                    () -> new Exception404("존재하지 않는 상위 글입니다."));
        }

        // 4. 같은 pageId를 가진 Post들 중에 입력받은 order보다 높은 모든 Post들의 order를 1씩 증가
        postJPARepository.findPostsByPageIdAndOrderGreaterThan(pageId, order).stream().forEach(p -> p.plusOrder());

        // 5. Post 엔티티 생성하고 저장하기
        Post newPost = Post.builder()
                .parent(parent)
                .orders(order)
                .groupMember(activeGroupMember)
                .title(title)
                .content(content)
                .created_at(LocalDateTime.now())
                .build();
        pageInfo.addPost(newPost);
        Post savedPost = postJPARepository.save(newPost);

        // 6. 히스토리 생성
        History newHistory = History.builder()
                .post(savedPost)
                .build();
        savedPost.addHistory(newHistory);
        historyJPARepository.save(newHistory);

        // 7. return DTO
        return new PostResponse.createPostDTO(savedPost);
    }

    @Transactional
    public PostResponse.modifyPostDTO modifyPost(Long memberId , Long groupId, Long postId , String title, String content){

        // 1. 존재하는 Member, Group, GroupMember 인지 fetch join으로 하나의 쿼리로 확인
        ActiveGroupMember activeGroupMember = checkGroupMember(memberId, groupId);

        // 2. postId로 post 엔티티 가져오기
        Post post = checkPostFromPostId(postId);

        // 3. 현재 Post랑 내용 같은지 확인
        if(post.getTitle().equals(title) && post.getContent().equals(content)){
            throw new Exception400("기존 글과 동일한 글입니다.");
        }

        // 4. 다르면 Post 수정후 히스토리 생성 저장
        History newHistory = post.modifyPost(activeGroupMember, title, content);
        historyJPARepository.save(newHistory);

        // 5. return DTO
        return new PostResponse.modifyPostDTO(post);
    }

    @Transactional
    public PostResponse.getPostHistoryDTO getPostHistory(Long memberId, Long groupId, Long postId , int pageNo){

        // 1. 존재하는 Member, Group, GroupMember 인지 fetch join으로 하나의 쿼리로 확인
        checkGroupMember(memberId, groupId);

        // 2. postId로 post 엔티티 가져오기
        Post post = checkPostFromPostId(postId);

        // 3. 해당 PostId로 history 모두 가져오기 시간순 + 페이지네이션
        Page<History> historys = historyJPARepository.findHistoryWithMemberByPostId(postId, PageRequest.of(pageNo, HISTORY_COUNT));

        // 4. DTO로 return
        List<PostResponse.getPostHistoryDTO.historyDTO> historyDTOs = historys.getContent().stream().
                map(h -> new PostResponse.getPostHistoryDTO.historyDTO(h.getGroupMember(),h)).collect(Collectors.toList());
        return new PostResponse.getPostHistoryDTO(post,historyDTOs);

    }

    @Transactional
    public PostResponse.deletePostDTO deletePost(Long memberId, Long groupId, Long postId){

        // 1. 존재하는 Member, Group, GroupMember 인지 fetch join으로 하나의 쿼리로 확인
        checkGroupMember(memberId, groupId);

        // 2. postId로 post 엔티티 가져오기
        Post post = checkPostFromPostId(postId);

        // 3. parent로 해당 postId를 가지고 있는 post가 있는지 확인 -> 존재하면 Exception
        if(postJPARepository.existsByParentId(postId)){
            throw new Exception400("하위 글이 존재하는 글은 삭제가 불가능합니다.");
        }

        // 4. child post 존재 안하면 history + post 삭제 시키기
        PostResponse.deletePostDTO response = new PostResponse.deletePostDTO(post);
        postJPARepository.deleteById(postId);

        // 5. order값 앞으로 땡기기
        postJPARepository.findPostsByPageIdAndOrderGreaterThan(post.getPageInfo().getId(), post.getOrders())
                .stream().forEach(p -> p.minusOrder());

        // 6. return DTO;
        return response;

    }

    @Transactional
    public PostResponse.createReportDTO createReport(Long memberId, Long groupId, Long postId , String content){

        // 1. 존재하는 Member, Group, GroupMember 인지 fetch join으로 하나의 쿼리로 확인
        ActiveGroupMember activeGroupMember = checkGroupMember(memberId, groupId);

        // 2. postId로 post 엔티티 가져오기
        checkPostFromPostId(postId);

        // 3. postId의 최근 히스토리 가져오기
        List<History> historyByPostId = historyJPARepository.findHistoryByPostId(postId, PageRequest.of(0, 1));

        // 4. report 생성
        Report report = Report.builder()
                .groupMember(activeGroupMember)
                .history(historyByPostId.get(0))
                .content(content)
                .created_at(LocalDateTime.now())
                .build();
        Report savedReport = reportJPARepository.save(report);

        // 5. return DTO
        return new PostResponse.createReportDTO(savedReport);

    }


    public ActiveGroupMember checkGroupMember(Long memberId, Long groupId){

        ActiveGroupMember activeGroupMember = groupMemberJPARepository.findActiveGroupMemberByMemberIdAndGroupIdFetchJoin(memberId, groupId)
                .orElseThrow(() -> new Exception404("해당 그룹에 속한 회원이 아닙니다."));
        if(activeGroupMember.getMember() == null) throw new Exception404("존재하지 않는 회원입니다.");
        if(activeGroupMember.getGroup() == null) throw new Exception404("존재하지 않는 그룹입니다.");

        return activeGroupMember;
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
