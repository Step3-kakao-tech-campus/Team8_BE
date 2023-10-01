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

}
