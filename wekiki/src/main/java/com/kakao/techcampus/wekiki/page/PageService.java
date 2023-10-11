package com.kakao.techcampus.wekiki.page;

import com.kakao.techcampus.wekiki._core.errors.ApplicationException;
import com.kakao.techcampus.wekiki._core.errors.ErrorCode;
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
public class PageService {

    private final PageJPARepository pageJPARepository;

    final int PAGE_COUNT = 10;

    @Transactional
    public PageInfoResponse.createPageDTO createPage(String title, Long groupId, Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. Page 생성
        PageInfo newPageInfo = PageInfo.builder()
                //.group(group)
                .title(title)
                .goodCount(0)
                .badCount(0)
                .viewCount(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        // 4. Page 저장
        PageInfo savedPageInfo = pageJPARepository.save(newPageInfo);

        // 5. return
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



}
