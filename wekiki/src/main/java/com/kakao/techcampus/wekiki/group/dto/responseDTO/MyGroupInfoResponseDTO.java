package com.kakao.techcampus.wekiki.group.dto.responseDTO;


import com.kakao.techcampus.wekiki.group.domain.Group;
import com.kakao.techcampus.wekiki.group.domain.member.GroupMember;
import com.kakao.techcampus.wekiki.history.History;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MyGroupInfoResponseDTO {

    private String groupName;
    private String groupNickName;
    private List<MyHistoryDTO> myHistoryDTOS;

    public MyGroupInfoResponseDTO(Group group, GroupMember groupMember, Page<History> histories) {
        this.groupName = group.getGroupName();
        this.groupNickName = groupMember.getNickName();
        this.myHistoryDTOS = histories.stream().map(MyHistoryDTO::new).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class MyHistoryDTO {
        private String content;
        private LocalDateTime created_at;

        public MyHistoryDTO(History history) {
            this.content = history.getContent();
            this.created_at = history.getCreated_at();
        }
    }
}
