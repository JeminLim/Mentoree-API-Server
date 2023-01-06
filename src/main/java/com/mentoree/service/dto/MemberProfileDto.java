package com.mentoree.service.dto;

import com.mentoree.domain.entity.Career;
import com.mentoree.domain.entity.History;
import com.mentoree.domain.entity.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileDto {

    private Long id;
    private String email;
    private String nickname;
    private String username;
    private List<History> histories = new ArrayList<>();

    public static MemberProfileDto of(Member member) {
        return MemberProfileDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .username(member.getUsername())
                .histories(member.getCareers().stream().map(Career::getHistory).collect(Collectors.toList()))
                .build();
    }

}
