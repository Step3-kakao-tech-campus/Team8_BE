package com.kakao.techcampus.wekiki.page;

import com.kakao.techcampus.wekiki._core.errors.ApplicationException;
import com.kakao.techcampus.wekiki._core.errors.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PageService {

    private final PageJPARepository pageJPARepository;

    @Transactional
    public PageResponse.createPageDTO createPage(String title, Long groupId, Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. Page 생성
        Page newPage = Page.builder()
                //.group(group)
                .title(title)
                .goodCount(0)
                .badCount(0)
                .viewCount(0)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        // 4. Page 저장
        Page savedPage = pageJPARepository.save(newPage);

        // 5. return
        return new PageResponse.createPageDTO(savedPage);
    }

    @Transactional
    public PageResponse.likePageDTO likePage(Long pageId , Long groupId, Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. 해당 페이지 불러오기 (없으면 Exception)
        Page page = pageJPARepository.findById(pageId).orElseThrow(() -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));

        // 4. 페이지 goodCount 증가
        page.plusGoodCount();

        // 5. 유저 경험치증가 or 유저 당일 페이지 좋아요 횟수 차감

        // 6. return
        return new PageResponse.likePageDTO(page);

    }

    @Transactional
    public PageResponse.hatePageDTO hatePage(Long pageId , Long groupId, Long userId){

        // 1. groupId랑 userId로 Group 객체, User 객체 가져오기 (없으면 Exception)

        // 2. groupMember 존재하는지 확인 (없으면 Exception)

        // 3. 해당 페이지 불러오기 (없으면 Exception)
        Page page = pageJPARepository.findById(pageId).orElseThrow(() -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));

        // 4. 페이지 goodCount 증가
        page.plusBadCount();

        // 5. 유저 경험치증가 or 유저 당일 페이지 좋아요 횟수 차감

        // 6. return
        return new PageResponse.hatePageDTO(page);
    }

}
