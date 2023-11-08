package com.kakao.techcampus.wekiki.post;

import com.kakao.techcampus.wekiki.comment.Comment;
import com.kakao.techcampus.wekiki.group.domain.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
import com.kakao.techcampus.wekiki.page.PageInfo;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private GroupMember groupMember;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private PageInfo pageInfo;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> historys  = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments  = new ArrayList<>();

    private String title;
    @Column(columnDefinition = "TEXT")
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

    public History modifyPost(GroupMember groupMember, String title, String content){
        this.groupMember = groupMember;
        this.title = title;
        this.content = content;
        this.created_at = LocalDateTime.now();

        History newHistory = History.builder()
                .post(this)
                .build();

        this.historys.add(newHistory);
        newHistory.setPost(this);

        return newHistory;
    }

    public void updateGroupMember(GroupMember groupMember) {
        this.groupMember = groupMember;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void addHistory(History history){
        this.historys.add(history);
        history.setPost(this);
    }
}
