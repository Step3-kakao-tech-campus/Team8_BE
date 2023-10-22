package com.kakao.techcampus.wekiki.group.groupDTO.responseDTO;

import com.kakao.techcampus.wekiki.history.History;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MyGroupHistoryResponseDTO {
    private List<MyGroupInfoResponseDTO.MyHistoryDTO> myHistoryDTOS;

    public MyGroupHistoryResponseDTO(Page<History> histories) {

        this.myHistoryDTOS = histories.stream().map(MyGroupInfoResponseDTO.MyHistoryDTO::new).collect(Collectors.toList());
    }
}
