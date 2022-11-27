package com.mentoree.service.dto;

import com.mentoree.domain.entity.Program;
import com.mentoree.domain.entity.ProgramState;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramInfoDto {

    private Long id;
    private String programName;
    private String description;
    private Integer maxMember;
    private Integer price;
    private LocalDate dueDate;
    private String category;
    private List<MentorDto> mentorList = new ArrayList<>();

    public static ProgramInfoDto of(Program program) {
        return ProgramInfoDto.builder()
                .id(program.getId())
                .programName(program.getProgramName())
                .description(program.getDescription())
                .price(program.getPrice())
                .maxMember(program.getMaxMember())
                .category(program.getCategory().getCategoryName())
                .mentorList(program.getMentor().stream().map(MentorDto::of).collect(Collectors.toList()))
                .dueDate(program.getDueDate())
                .build();
    }





}
