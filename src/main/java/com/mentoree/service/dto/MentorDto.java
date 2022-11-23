package com.mentoree.service.dto;

import com.mentoree.domain.entity.Mentor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorDto {

    private Long memberId;
    private Long programId;
    private String username;
    private String programName;
    private Boolean host;

    public static MentorDto of(Mentor mentor) {
        return MentorDto.builder()
                .memberId(mentor.getMember().getId())
                .programId(mentor.getProgram().getId())
                .username(mentor.getMember().getUsername())
                .programName(mentor.getProgram().getProgramName())
                .host(mentor.getHost())
                .build();
    }

}
