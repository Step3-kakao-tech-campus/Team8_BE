package com.kakao.techcampus.wekiki.page;

import com.kakao.techcampus.wekiki._core.errors.ApplicationException;
import com.kakao.techcampus.wekiki._core.errors.ErrorCode;
import com.kakao.techcampus.wekiki._core.utils.IndexUtils;
import com.kakao.techcampus.wekiki.post.Post;
import com.kakao.techcampus.wekiki.post.PostJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PageService {

    private final PageJPARepository pageJPARepository;
    private final PostJPARepository postJPARepository;

    private final IndexUtils indexUtils;

    final int PAGE_COUNT = 10;

    @Transactional
    public PageInfoResponse.deletePageDTO deletePage(Long userId, Long pageId){
        // 1. userId로 User 객체 가져오기

        // 2. GroupMember 인지 체크하기

        // 3. 존재하는 페이지 인지 체크
        PageInfo pageInfo = pageJPARepository.findById(pageId).orElseThrow(() -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));

        // 3. pageId로 post 있는지 확인 -> post 존재하면 Exception
        if(postJPARepository.existsByPageInfoId(pageId)){
            throw new ApplicationException(ErrorCode.PAGE_HAVE_POST);
        }

        // 4. 포스트가 하나도 없으면 삭제시키기
        PageInfoResponse.deletePageDTO response = new PageInfoResponse.deletePageDTO(pageInfo);
        pageJPARepository.deleteById(pageId);

        // 5. return DTO
        return response;
    }


    @Transactional
    public PageInfoResponse.getPageFromIdDTO getPageFromId(Long userId, Long pageId){
        // 1. userId로 User 객체 가져오기

        // 2. pageId로 PageInfo 객체 들고오기
        PageInfo pageInfo = pageJPARepository.findById(pageId).orElseThrow(() -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));

        // 3. PageInfo로부터 Group 객체 들고오기

        // 4. GroupMember인지 체크

        // 5. 해당 groupId를 들고 있는 모든 페이지 Order 순으로 들고오기
        List<Post> posts = postJPARepository.findPostsByPageIdOrderByOrderAsc(pageId);

        // 6. 목차 생성하기
        HashMap<Long, String> indexs = indexUtils.createIndex(posts);

        // 7. DTO로 return
        List<PageInfoResponse.getPageFromIdDTO.postDTO> temp = posts.stream()
                .map(p -> new PageInfoResponse.getPageFromIdDTO.postDTO(p, indexs.get(p.getId())))
                .collect(Collectors.toList());

        return new PageInfoResponse.getPageFromIdDTO(pageInfo, temp);

    }

    @Transactional
    public PageInfoResponse.createPageDTO createPage(String title, Long groupId, Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. 그룹 내 동일한 title의 Page가 존재하는지 체크 (TODO : where 문에 groupId 추가)
        if(pageJPARepository.findByTitle(title).isPresent()){
            throw new ApplicationException(ErrorCode.PAGE_ALREADY_PRESENT);
        }

        // 4. Page 생성
        PageInfo newPageInfo = PageInfo.builder()
                //.group(group)
                .title(title)
                .goodCount(0)
                .badCount(0)
                .viewCount(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        // 5. Page 저장
        PageInfo savedPageInfo = pageJPARepository.save(newPageInfo);

        // 6. return DTO
        return new PageInfoResponse.createPageDTO(savedPageInfo);
    }

    @Transactional
    public PageInfoResponse.likePageDTO likePage(Long pageId , Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. 해당 페이지 불러오기 (없으면 Exception)
        PageInfo pageInfo = pageJPARepository.findById(pageId).orElseThrow(() -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));

        // 4. 페이지 goodCount 증가
        pageInfo.plusGoodCount();

        // 5. 유저 경험치증가 or 유저 당일 페이지 좋아요 횟수 차감

        // 6. return
        return new PageInfoResponse.likePageDTO(pageInfo);

    }

    @Transactional
    public PageInfoResponse.hatePageDTO hatePage(Long pageId , Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. 해당 페이지 불러오기 (없으면 Exception)
        PageInfo pageInfo = pageJPARepository.findById(pageId).orElseThrow(() -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));

        // 4. 페이지 goodCount 증가
        pageInfo.plusBadCount();

        // 5. 유저 경험치증가 or 유저 당일 페이지 좋아요 횟수 차감

        // 6. return
        return new PageInfoResponse.hatePageDTO(pageInfo);
    }

    @Transactional
    public List<PageInfoResponse.searchPageDTO> searchPage(int pageNo, String keyword){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. keyword로 존재하는 page title에 keyword를 가지고 있는 페이지들 다 가져오기
        PageRequest pageRequest = PageRequest.of(pageNo, PAGE_COUNT);
        Page<PageInfo> pages = pageJPARepository.findPagesByTitleContainingKeyword(keyword, pageRequest);

        // 4. pages로 DTO return
        return pages.getContent().stream().map(pageInfo -> new PageInfoResponse.searchPageDTO(pageInfo)).collect(Collectors.toList());

    }

    // ============================groupId 필요==========================================================

    @Transactional
    public PageInfoResponse.getRecentPageDTO getRecentPage(Long userId , Long groupId){

        // 1. userId user 객체 들고오기

        // 2. groupId로부터 Group 객체 들고오기

        // 3. userId랑 groupId로 groupMember 존재하는지 확인

        // 4. 특정 groupId를 가진 Page들 order by로 updated_at이 최신인 10개 Page 조회
        // TODO : where 문에 groupId 추가
        Pageable pageable = PageRequest.of(0, 10);
        List<PageInfo> recentPage = pageJPARepository.findOrderByUpdatedAtDesc(pageable);
        //List<PageInfo> recentPage = pageJPARepository.findByGroupIdOrderByUpdatedAtDesc(groupId, pageable);

        // 5. return DTO
        List<PageInfoResponse.getRecentPageDTO.RecentPageDTO> collect = recentPage.stream().map(pageInfo ->
                new PageInfoResponse.getRecentPageDTO.RecentPageDTO(pageInfo)).collect(Collectors.toList());
        return new PageInfoResponse.getRecentPageDTO(collect);

    }


}
