package com.mentoree.domain.entity;

import lombok.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    //=== 멘티 회원 필수 정보 ===//
    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private UserRole role;
    private String email;
    private String nickname;
    private String userPassword;
    private String oAuthId;

    //=== 멘토 회원 필수 정보 ===//
    private String username;

    @OneToMany(orphanRemoval = true, mappedBy = "member")
    private List<Career> careers = new ArrayList<>();

    //== 탈퇴 요청 여부 ==//
    private Boolean withdrawal;

    @Builder
    public Member(String username, String email, String oAuthId, String nickname,
                  String userPassword, UserRole role) {
        Assert.notNull(email, "email must not be null");

        this.username = username;
        this.oAuthId = oAuthId;
        this.email = email;
        this.nickname = nickname;
        this.userPassword = userPassword;
        this.role = role;
        this.withdrawal = false;
    }

    //== 비지니스 로직 ==//
    public void updateNickname(String nickname) {
        if(!this.nickname.equals(nickname))
            this.nickname = nickname;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateCareer(Career career) {
        this.careers.add(career);
    }

    public void transformMentor() {
        this.role = UserRole.MENTOR;
    }

    public void requestWithdraw() {
        if(this.withdrawal)
            throw new IllegalArgumentException("중복된 요청입니다.");

        this.withdrawal = true;
    }

}
