package com.kakao.techcampus.wekiki.page;

import com.kakao.techcampus.wekiki._core.error.exception.Exception400;
import com.kakao.techcampus.wekiki._core.error.exception.Exception404;
import com.kakao.techcampus.wekiki._core.utils.IndexUtils;
import com.kakao.techcampus.wekiki._core.utils.RedisUtility;
import com.kakao.techcampus.wekiki.group.Group;
import com.kakao.techcampus.wekiki.group.GroupJPARepository;
import com.kakao.techcampus.wekiki.group.member.ActiveGroupMember;
import com.kakao.techcampus.wekiki.group.member.GroupMemberJPARepository;
import com.kakao.techcampus.wekiki.member.Member;
import com.kakao.techcampus.wekiki.member.MemberJPARepository;
import com.kakao.techcampus.wekiki.post.Post;
import com.kakao.techcampus.wekiki.post.PostJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class PageService {

    private final PageJPARepository pageJPARepository;
    private final PostJPARepository postJPARepository;
    private final MemberJPARepository memberJPARepository;
    private final GroupMemberJPARepository groupMemberJPARepository;
    private final GroupJPARepository groupJPARepository;
    private final IndexUtils indexUtils;

    private final RedisUtility redisUtility;

    final int PAGE_COUNT = 10;

    @Transactional
    public PageInfoResponse.getPageIndexDTO getPageIndex(Long groupId,Long memberId, Long pageId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. pageId로 PageInfo 객체 들고오기
        PageInfo pageInfo = pageJPARepository.findByPageIdWithPosts(pageId)
                .orElseThrow(() -> new Exception404("존재하지 않는 페이지 입니다."));

        // 5. 해당 groupId를 들고 있는 모든 페이지 Order 순으로 들고오기
        List<Post> posts = pageInfo.getPosts();

        // 6. 목차 생성하기
        HashMap<Long, String> indexs = indexUtils.createIndex(posts);

        // 7. DTO로 return
        List<PageInfoResponse.getPageIndexDTO.postDTO> temp = posts.stream()
                .map(p -> new PageInfoResponse.getPageIndexDTO.postDTO(p, indexs.get(p.getId())))
                .collect(Collectors.toList());

        return new PageInfoResponse.getPageIndexDTO(pageInfo, temp);

    }

    @Transactional
    public PageInfoResponse.deletePageDTO deletePage(Long memberId, Long groupId, Long pageId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. 존재하는 페이지 인지 체크
        PageInfo pageInfo = checkPageFromPageId(pageId);

        // 5. pageId로 하위 post들이 존재하는지 확인 -> 존재하면 Exception
        if(postJPARepository.existsByPageInfoId(pageId)){
            throw new Exception400("글이 적혀있는 페이지는 삭제가 불가능합니다.");
        }

        // 6. 포스트가 하나도 없으면 삭제시키기
        PageInfoResponse.deletePageDTO response = new PageInfoResponse.deletePageDTO(pageInfo);
        pageJPARepository.deleteById(pageId);

        // 7. redis에 페이지 목록 삭제 시켜주기
        redisUtility.deleteValues(groupId+"_"+pageInfo.getPageName());

        // 8. return DTO
        return response;
    }


    @Transactional
    public PageInfoResponse.getPageFromIdDTO getPageFromId(Long memberId,Long groupId, Long pageId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. pageId로 PageInfo 객체 들고오기
        PageInfo pageInfo = pageJPARepository.findByPageIdWithPosts(pageId)
                .orElseThrow(() -> new Exception404("존재하지 않는 페이지 입니다."));

        // 5. 해당 groupId를 들고 있는 모든 페이지 Order 순으로 들고오기
        List<Post> posts = pageInfo.getPosts();

        // 6. 목차 생성하기
        HashMap<Long, String> indexs = indexUtils.createIndex(posts);

        // 7. DTO로 return
        List<PageInfoResponse.getPageFromIdDTO.postDTO> temp = posts.stream()
                .map(p -> new PageInfoResponse.getPageFromIdDTO.postDTO(p, indexs.get(p.getId())))
                .collect(Collectors.toList());

        return new PageInfoResponse.getPageFromIdDTO(pageInfo, temp);

    }

    @Transactional
    public PageInfoResponse.createPageDTO createPage(String title, Long groupId, Long memberId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        Group group = checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. 그룹 내 동일한 title의 Page가 존재하는지 체크
        if(pageJPARepository.findByTitle(groupId,title).isPresent()){
            throw new Exception400("이미 존재하는 페이지입니다.");
        }

        // 5. Page 생성
        PageInfo newPageInfo = PageInfo.builder()
                .group(group)
                .pageName(title)
                .goodCount(0)
                .badCount(0)
                .viewCount(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        // 5. Page 저장
        PageInfo savedPageInfo = pageJPARepository.save(newPageInfo);

        // TODO : 추후에 redis value 자료구조를 String에서 Hash로 변경 (key overhead 최소화)
        //       <groupId, <pageTitle,pageId>>
        redisUtility.setValues(groupId+"_"+title,newPageInfo.getId().toString());

        // 6. return DTO
        return new PageInfoResponse.createPageDTO(savedPageInfo);
    }

    @Transactional
    public PageInfoResponse.likePageDTO likePage(Long pageId , Long groupId, Long memberId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        Group group = checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. 존재하는 페이지 인지 체크
        PageInfo pageInfo = checkPageFromPageId(pageId);

        // 5. 페이지 goodCount 증가
        pageInfo.plusGoodCount();

        // TODO : 6. 유저 경험치증가 or 유저 당일 페이지 좋아요 횟수 차감

        // 7. return DTO
        return new PageInfoResponse.likePageDTO(pageInfo);

    }

    @Transactional
    public PageInfoResponse.hatePageDTO hatePage(Long pageId , Long groupId, Long memberId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        Group group = checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. 존재하는 페이지 인지 체크
        PageInfo pageInfo = checkPageFromPageId(pageId);

        // 5. 페이지 goodCount 증가
        pageInfo.plusBadCount();

        // TODO : 6. 유저 경험치증가 or 유저 당일 페이지 좋아요 횟수 차감

        // 7. return DTO
        return new PageInfoResponse.hatePageDTO(pageInfo);
    }

    @Transactional
    public PageInfoResponse.searchPageDTO searchPage(Long groupId, Long memberId, int pageNo, String keyword){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        Group group = checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. keyword로 존재하는 page title에 keyword를 가지고 있는 페이지들 다 가져오기
        // (TODO : fetch join이랑 Pagination 함께 쓰면 페이지네이션을 애플리케이션에 들고온 다음 하게됨)
        Page<PageInfo> pages = pageJPARepository.findPagesWithPosts(groupId, keyword, PageRequest.of(pageNo, PAGE_COUNT));

        // 5. 해당 Page들을 FK로 가지고 있는 Post들 중에 Orders가 1인 Post들 가져오기
        List<PageInfoResponse.searchPageDTO.pageDTO> res = new ArrayList<>();

        for(PageInfo p : pages.getContent()){
            List<Post> posts = p.getPosts();
            if(posts.size() == 0){
                res.add(new PageInfoResponse.searchPageDTO.pageDTO(p,""));
            }else{
                res.add(new PageInfoResponse.searchPageDTO.pageDTO(p,posts.get(0).getContent()));
            }
        }

        // 6. pages로 DTO return
        return new PageInfoResponse.searchPageDTO(res);
    }

    @Transactional
    public PageInfoResponse.getRecentPageDTO getRecentPage(Long memberId , Long groupId){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. 특정 groupId를 가진 Page들 order by로 updated_at이 최신인 10개 Page 조회
        Pageable pageable = PageRequest.of(0, 10);
        List<PageInfo> recentPage = pageJPARepository.findByGroupIdOrderByUpdatedAtDesc(groupId, pageable);

        // 5. return DTO
        List<PageInfoResponse.getRecentPageDTO.RecentPageDTO> collect = recentPage.stream().map(pageInfo ->
                new PageInfoResponse.getRecentPageDTO.RecentPageDTO(pageInfo)).collect(Collectors.toList());
        return new PageInfoResponse.getRecentPageDTO(collect);

    }

    @Transactional
    public PageInfoResponse.getPageFromIdDTO getPageFromTitle(Long memberId, Long groupId, String title){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. groupId랑 title로 Page있는지 확인 (fetch join으로 post들 가져오기)
        PageInfo page = pageJPARepository.findByTitleWithPosts(groupId,title).
                orElseThrow(() -> new Exception404("존재하지 않는 페이지 입니다."));

        // 5. 해당 pageId를 들고있는 모든 페이지 Order 순으로 들고오기
        List<Post> posts = page.getPosts();

        // 6. 목차 생성하기
        HashMap<Long, String> indexs = indexUtils.createIndex(posts);

        // 7. DTO로 return
        List<PageInfoResponse.getPageFromIdDTO.postDTO> temp = posts.stream()
                .map(p -> new PageInfoResponse.getPageFromIdDTO.postDTO(p, indexs.get(p.getId())))
                .collect(Collectors.toList());

        return new PageInfoResponse.getPageFromIdDTO(page, temp);
    }

    @Transactional
    public PageInfoResponse.getPageLinkDTO getPageLink(Long memberId, Long groupId, String title){

        // 1. memberId로 Member 객체 가져오기
        checkMemberFromMemberId(memberId);

        // 2. 존재하는 group인지 확인하기
        Group group = checkGroupFromGroupId(groupId);

        // 3. 해당 그룹에 속하는 Member인지 확인 (=GroupMember 확인)
        checkGroupMember(memberId, groupId);

        // 4. redis로 groupId_title을 key로 value 받아오기 (페이지 테이블에 접근할 필요 x)
        String value = redisUtility.getValues(groupId+"_"+title);
        if(value == null){
            throw new Exception404("존재하지 않는 페이지 입니다.");
        }else{
            // 5. return DTO
            return new PageInfoResponse.getPageLinkDTO(Long.valueOf(value));
        }

        // 4. groupId랑 title로 Page있는지 확인 - where 문에 groupId 추가
        //  PageInfo page = pageJPARepository.findByTitle(title).
        //          orElseThrow(() -> new Exception404("존재하지 않는 페이지 입니다."));

        // 5. return DTO
        //return new PageInfoResponse.getPageLinkDTO(page);
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

        /*
            TODO : groupMember 들고올때 fetch join으로 member랑 group도 들고오기
            여기서
            if(activeGroupMember.getMember() != null) throw new Exception404("존재하지 않는 회원입니다.");
            if(activeGroupMember.getGroup() != null) throw new Exception404("존재하지 않는 그룹입니다.");
            체크 추가해주기
         */

        return groupMemberJPARepository.findGroupMemberByMemberIdAndGroupId(memberId,groupId)
                .orElseThrow(() -> new Exception404("해당 그룹에 속한 회원이 아닙니다."));
    }

    public PageInfo checkPageFromPageId(Long pageId){
        return pageJPARepository.findById(pageId)
                .orElseThrow(() -> new Exception404("존재하지 않는 페이지 입니다."));
    }
}
