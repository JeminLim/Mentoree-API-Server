package com.mentoree.service.dto;

import com.mentoree.domain.entity.Mentee;
import com.mentoree.domain.entity.Mentor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeDto {

    private Long memberId;
    private Long programId;
    private String username;
    private String programName;

    public static MenteeDto of(Mentee mentee) {
        return MenteeDto.builder()
                .memberId(mentee.getMember().getId())
                .programId(mentee.getProgram().getId())
                .username(mentee.getMember().getUsername())
                .programName(mentee.getProgram().getProgramName())
                .build();
    }

}
