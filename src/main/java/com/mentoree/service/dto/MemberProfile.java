package com.mentoree.service.dto;

import com.mentoree.domain.entity.Career;
import com.mentoree.domain.entity.Member;
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
public class MemberProfile {

    private Long id;
    private String email;
    private String nickname;
    private String username;
    private List<History> careerList = new ArrayList<>();

    public static MemberProfile of(Member member) {
        return MemberProfile.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .username(member.getUsername())
                .careerList(member.getCareers().stream()
                        .map(career -> new History(career.getCompanyName(), career.getStartDate(), career.getEndDate(), career.getPosition()))
                        .collect(Collectors.toList())
                ).build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class History {
        private String companyName;
        private LocalDate start;
        private LocalDate end;
        private String position;

        public Career toCareerEntity() {
            return Career.builder()
                    .companyName(this.companyName)
                    .startDate(this.start)
                    .endDate(this.end)
                    .position(this.position)
                    .build();
        }

    }


}
