package com.mentoree.service.dto;


import com.mentoree.domain.entity.Mission;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionInfoDto {

    private Long id;
    private Long programId;
    private Long writerId;
    private String writerNickname;
    private String title;
    private String content;
    private LocalDate dueDate;

    public static MissionInfoDto of(Mission mission) {
        return MissionInfoDto.builder()
                .id(mission.getId())
                .programId(mission.getProgram().getId())
                .writerId(mission.getWriter().getId())
                .writerNickname(mission.getWriter().getNickname())
                .title(mission.getWriting().getTitle())
                .content(mission.getWriting().getContent())
                .dueDate(mission.getDueDate())
                .build();
    }


}
