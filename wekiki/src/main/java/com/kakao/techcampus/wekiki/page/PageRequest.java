package com.kakao.techcampus.wekiki.page;

import lombok.Getter;
import lombok.Setter;

public class PageRequest {

    @Getter
    @Setter
    public static class createPageDTO {
        private String title;
        private Long groupId;
    }

}
