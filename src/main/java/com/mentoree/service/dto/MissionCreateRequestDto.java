package com.mentoree.service.dto;

import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Mission;
import com.mentoree.domain.entity.Program;
import com.mentoree.domain.entity.Writing;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionCreateRequestDto {

    @NotNull
    private Long programId;
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private LocalDate dueDate;

    public Mission toEntity(Program program, Member writer) {
        Writing writing = new Writing(title, description);
        return Mission.builder()
                .program(program)
                .writer(writer)
                .writing(writing)
                .dueDate(dueDate)
                .build();
    }

}
