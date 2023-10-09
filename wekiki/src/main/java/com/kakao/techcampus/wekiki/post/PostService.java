package com.kakao.techcampus.wekiki.post;


import com.kakao.techcampus.wekiki._core.errors.ApplicationException;
import com.kakao.techcampus.wekiki._core.errors.ErrorCode;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.history.HistoryJPARepository;
import com.kakao.techcampus.wekiki.page.PageInfo;
import com.kakao.techcampus.wekiki.page.PageJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PageJPARepository pageJPARepository;
    private final PostJPARepository postJPARepository;

    private final HistoryJPARepository historyJPARepository;

    @Transactional
    public PostResponse.createPostDTO createPost(Long pageId, Long parentPostId, int order, String title, String content){

        // 1. userId로 user 객체 들고오기

        // 2. pageId로 해당 PageInfo 객체들고오고 update_at 바꾸기 group 객체 가져오기
        PageInfo pageInfo = pageJPARepository.findById(pageId).orElseThrow(
                () -> new ApplicationException(ErrorCode.PAGE_NOT_FOUND));
        pageInfo.updatePage();

        // 3. userId랑 groupId로 groupmember가 맞는지 확인

        // 4. parentPostId로 parentPost 가져오기
        Post parent = null;
        if(parentPostId != 0) {
            parent = postJPARepository.findById(parentPostId).orElseThrow(
                    () -> new ApplicationException(ErrorCode.PARENT_POST_NOT_FOUND));
        }

        // 5. 같은 pageId를 가진 Post들 중에 입력받은 order보다 높은 모든 Post들의 order를 1씩 증가
        List<Post> postsByPageIdAndOrderGreaterThan = postJPARepository.findPostsByPageIdAndOrderGreaterThan(pageId, order);
        for(Post p : postsByPageIdAndOrderGreaterThan){
            p.plusOrder();
        }

        // 6. Post 엔티티 생성하고 저장하기
        Post newPost = Post.builder()
                .parent(parent)
                .orders(order)
                //.groupMember()
                .pageInfo(pageInfo)
                .title(title)
                .content(content)
                .created_at(LocalDateTime.now())
                .build();

        Post savedPost = postJPARepository.save(newPost);

        // 7. 히스토리 생성
        History newHistory = History.builder()
                .post(savedPost)
                .build();

        historyJPARepository.save(newHistory);

        // 8. return DTO
        return new PostResponse.createPostDTO(savedPost);
    }

    @Transactional
    public PostResponse.modifyPostDTO modifyPost(Long postId , String title, String content){

        // 1. userId user 객체 들고오기

        // 2. postId -> pageId -> groupId (여기서 groupId 바로 받기..?)

        // 3. userId랑 groupId로 groupMember 있는지 확인 (즉 그룹 멤버인지 확인)

        // 4. postId로 post 엔티티 가져오기
        Post post = postJPARepository.findById(postId).orElseThrow(
                () -> new ApplicationException(ErrorCode.POST_NOT_FOUND));

        // 5. 현재 Post랑 내용 같은지 확인
        if(post.getTitle().equals(title) && post.getContent().equals(content)){
            throw new ApplicationException(ErrorCode.POST_SAME_DATE);
        }

        // 6. 다르면 Post 수정후 히스토리 생성 저장
        // TODO : groupMember 추가
        post.modifyPost(null,title,content);

        History newHistory = History.builder()
                .post(post)
                .build();
        historyJPARepository.save(newHistory);

        // 7. return DTO
        return new PostResponse.modifyPostDTO(post);

    }

}
