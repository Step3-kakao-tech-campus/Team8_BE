package com.kakao.techcampus.wekiki.page;

import lombok.Getter;
import lombok.Setter;

public class PageInfoResponse {

    @Getter @Setter
    public static class createPageDTO{

        Long pageId;
        String title;

        public createPageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.title = pageInfo.getTitle();
        }
    }

    @Getter @Setter
    public static class likePageDTO{

        Long pageId;
        String title;
        int goodCount;

        public likePageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.title = pageInfo.getTitle();
            this.goodCount = pageInfo.getGoodCount();
        }
    }

    @Getter @Setter
    public static class hatePageDTO{

        Long pageId;
        String title;
        int badCount;

        public hatePageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.title = pageInfo.getTitle();
            this.badCount = pageInfo.getBadCount();
        }
    }

    @Getter @Setter
    public static class searchPageDTO{

        Long pageId;
        String title;
        //String contents;

        public searchPageDTO(PageInfo pageInfo){
            this.pageId = pageInfo.getId();
            this.title = pageInfo.getTitle();
        }

    }

}
