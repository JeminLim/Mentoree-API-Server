package com.mentoree.service.dto;

import com.mentoree.domain.entity.Category;
import com.mentoree.domain.entity.Program;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramCreateRequestDto {

    @NotEmpty
    private String programName;
    @NotEmpty
    private String description;
    @NotNull
    private Integer price;

    @NotNull
    private String categoryName;
    @NotNull
    @Min(1) @Max(5)
    private Integer maxMember;
    @NotNull
    private LocalDate dueDate;

    public Program toEntity(Category category) {
        return Program.builder()
                .programName(programName)
                .description(description)
                .price(price)
                .dueDate(dueDate)
                .maxMember(maxMember)
                .category(category)
                .build();

    }

}
