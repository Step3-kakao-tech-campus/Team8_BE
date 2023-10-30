package com.kakao.techcampus.wekiki.page;

import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pageinfo_tb")
public class PageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;
    private String pageName;

    @OneToMany(mappedBy = "pageInfo")
    private List<Post> posts = new ArrayList<>();
    private int goodCount;
    private int badCount;
    private int viewCount;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @Builder
    public PageInfo(Long id, Group group, String pageName, int goodCount, int badCount, int viewCount, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.group = group;
        this.pageName = pageName;
        this.goodCount = goodCount;
        this.badCount = badCount;
        this.viewCount = viewCount;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public void plusGoodCount(){
        this.goodCount++;
    }

    public void plusBadCount(){
        this.badCount++;
    }

    public void updatePage(){
        this.updated_at = LocalDateTime.now();
    }

    public void addPost(Post post){
        this.posts.add(post);
        post.setPageInfo(this);
    }


}
