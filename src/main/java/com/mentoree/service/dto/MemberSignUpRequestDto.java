package com.mentoree.service.dto;

import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.UserRole;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpRequestDto {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&#])[A-Za-z\\d$@!%*?&]{8,16}")
    private String password;
    @NotBlank
    @Size(min = 1, max = 10)
    private String nickname;

    public Member toEntity(String oAuthId, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .userPassword(passwordEncoder.encode(password))
                .nickname(nickname)
                .oAuthId(oAuthId)
                .role(UserRole.MENTEE)
                .build();
    }


}
