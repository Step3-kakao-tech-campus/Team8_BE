package com.kakao.techcampus.wekiki.page;

import com.kakao.techcampus.wekiki.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class PageInfoResponse {

    @Getter @Setter
    public static class deletePageDTO{

        Long pageId;
        String title;

        public deletePageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.title = pageInfo.getPageName();
        }
    }

    @Getter @Setter
    public static class createPageDTO{

        Long pageId;
        String pageName;

        public createPageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.pageName = pageInfo.getPageName();
        }
    }

    @Getter @Setter
    public static class likePageDTO{

        Long pageId;
        String pageName;
        int goodCount;

        public likePageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.pageName = pageInfo.getPageName();
            this.goodCount = pageInfo.getGoodCount();
        }
    }

    @Getter @Setter
    public static class hatePageDTO{

        Long pageId;
        String pageName;
        int badCount;

        public hatePageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.pageName = pageInfo.getPageName();
            this.badCount = pageInfo.getBadCount();
        }
    }

    @Getter @Setter
    public static class searchPageDTO{

        Long pageId;
        String pageName;
        //String contents;

        public searchPageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.pageName = pageInfo.getPageName();
        }

    }

    @Getter
    @Setter
    public static class getPageIndexDTO{
        String pageName;
        List<postDTO> postList;

        public getPageIndexDTO(PageInfo pageInfo , List<postDTO> postList){
            this.pageName = pageInfo.getPageName();
            this.postList = postList;
        }


        @Getter
        @Setter
        public static class postDTO {
            Long postId;
            String index;
            String postTitle;

            public postDTO(Post post, String index){
                this.postId = post.getId();
                this.index = index;
                this.postTitle = post.getTitle();
            }
        }
    }

    @Getter
    @Setter
    public static class getPageFromIdDTO{

        Long pageId;
        String pageName;
        List<postDTO> postList;
        int goodCount;
        int badCount;

        public getPageFromIdDTO(PageInfo pageInfo , List<postDTO> postList){
            this.pageId = pageInfo.getId();
            this.pageName = pageInfo.getPageName();
            this.postList = postList;
            this.goodCount = pageInfo.getGoodCount();
            this.badCount = pageInfo.getBadCount();
        }


        @Getter
        @Setter
        public static class postDTO {
            Long postId;
            String index;
            String postTitle;
            String content;

            public postDTO(Post post, String index){
                this.postId = post.getId();
                this.index = index;
                this.postTitle = post.getTitle();
                this.content = post.getContent();
            }
        }
    }


    @Getter @Setter
    public static class getRecentPageDTO{

        List<RecentPageDTO> recentPage;

        public getRecentPageDTO(List<RecentPageDTO> recentPage){
            this.recentPage = recentPage;
        }

        @Getter @Setter
        public static class RecentPageDTO{
            Long pageId;
            String pageName;
            private LocalDateTime updated_at;

            public RecentPageDTO(PageInfo pageInfo){
                this.pageId = pageInfo.getId();
                this.pageName = pageInfo.getPageName();
                this.updated_at = pageInfo.getUpdated_at();
            }
        }

    }

    @Getter @Setter
    public static class getPageLinkDTO{
        Long pageId;

        public getPageLinkDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
        }
    }

}
