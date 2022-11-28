package com.mentoree.service.dto;

import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpRequest {

    private String email;
    private String password;
    private String nickname;

    public Member toEntity(String oAuthId) {
        return Member.builder()
                .email(email)
                .userPassword(password)
                .nickname(nickname)
                .oAuthId(oAuthId)
                .role(UserRole.MENTEE)
                .build();
    }


}
