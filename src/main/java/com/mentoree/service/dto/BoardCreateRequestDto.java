package com.mentoree.service.dto;

import com.mentoree.domain.entity.Board;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Mission;
import com.mentoree.domain.entity.Writing;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateRequestDto {

    @NotNull
    private Long missionId;
    private String title;
    private String description;

    public Board toEntity(Mission mission, Member writer, boolean temporal) {
        Writing writing = new Writing(title, description);
        return Board.builder()
                .mission(mission)
                .writer(writer)
                .writing(writing)
                .temporal(temporal)
                .build();

    }
}
