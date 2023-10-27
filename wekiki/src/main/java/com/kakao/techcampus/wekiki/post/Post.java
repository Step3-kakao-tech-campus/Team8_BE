package com.kakao.techcampus.wekiki.post;

import com.kakao.techcampus.wekiki.comment.Comment;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.page.PageInfo;
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
@Table(name = "post_tb")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post parent;

    private int orders;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupMember groupMember;
    @ManyToOne(fetch = FetchType.LAZY)
    private PageInfo pageInfo;

    @OneToMany(mappedBy = "post")
    private List<History> historys  = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments  = new ArrayList<>();

    private String title;
    private String content;
    private LocalDateTime created_at;

    @Builder
    public Post(Long id ,Post parent,int orders, GroupMember groupMember, PageInfo pageInfo, String title, String content, LocalDateTime created_at) {
        this.id = id;
        this.parent = parent;
        this.orders = orders;
        this.groupMember = groupMember;
        this.pageInfo = pageInfo;
        this.title = title;
        this.content = content;
        this.created_at = created_at;
    }

    public void plusOrder(){
        this.orders++;
    }

    public void minusOrder(){
        this.orders--;
    }

    public void modifyPost(GroupMember groupMember, String title, String content){
        this.groupMember = groupMember;
        this.title = title;
        this.content = content;
        this.created_at = LocalDateTime.now();
    }

    public void updateGroupMember(GroupMember groupMember) {
        this.groupMember = groupMember;
    }
}
