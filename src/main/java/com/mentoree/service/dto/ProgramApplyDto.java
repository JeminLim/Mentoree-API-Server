package com.mentoree.service.dto;

import com.mentoree.domain.entity.Applicant;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Program;
import com.mentoree.domain.entity.ProgramRole;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramApplyDto {

    @NotNull
    private Long programId;
    @NotNull
    private String role;
    @NotEmpty
    private String message;

    public Applicant toEntity(Member member, Program program) {
        return Applicant.builder()
                .program(program)
                .member(member)
                .message(message)
                .role(ProgramRole.valueOf(role))
                .build();
    }

}
