package com.mentoree.service.dto;

import com.mentoree.domain.entity.Applicant;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Program;
import com.mentoree.domain.entity.ProgramRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantDto {

    private Long id;
    private String nickname;
    private String role;
    private String message;

    public static ApplicantDto of(Applicant applicant) {
        return ApplicantDto.builder()
                .id(applicant.getId())
                .nickname(applicant.getMember().getNickname())
                .role(applicant.getRole().getValue())
                .message(applicant.getMessage())
                .build();
    }
}
