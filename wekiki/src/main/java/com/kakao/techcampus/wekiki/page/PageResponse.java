package com.kakao.techcampus.wekiki.page;

import lombok.Getter;
import lombok.Setter;

public class PageResponse {

    @Getter @Setter
    public static class createPageDTO{

        Long pageId;
        String title;

        public createPageDTO(Page page){
            this.pageId = page.getId();
            this.title = page.getTitle();
        }
    }

    @Getter @Setter
    public static class likePageDTO{

        Long pageId;
        String title;
        int goodCount;

        public likePageDTO(Page page){
            this.pageId = page.getId();
            this.title = page.getTitle();
            this.goodCount = page.getGoodCount();
        }
    }

    @Getter @Setter
    public static class hatePageDTO{

        Long pageId;
        String title;
        int badCount;

        public hatePageDTO(Page page){
            this.pageId = page.getId();
            this.title = page.getTitle();
            this.badCount = page.getBadCount();
        }
    }

}
