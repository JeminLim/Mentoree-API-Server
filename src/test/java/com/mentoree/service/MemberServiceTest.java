package com.mentoree.service;

import com.mentoree.domain.entity.Career;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.UserRole;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.service.dto.MemberProfile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("멤버 정보 열람")
    void getProfileTest() {

        //given
        Member memberA = createMember("memberA");
        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(memberA));

        MemberProfile findMember = memberService.getProfile(1L);

        assertThat(findMember.getEmail()).isEqualTo(memberA.getEmail());
        assertThat(findMember.getNickname()).isEqualTo(memberA.getNickname());
        assertThat(findMember.getUsername()).isEqualTo(memberA.getUsername());
        assertThat(findMember.getCareerList().size()).isEqualTo(memberA.getCareers().size());
        assertThat(findMember.getCareerList().get(0).getCompanyName())
                .isEqualTo(memberA.getCareers().get(0).getCompanyName());
    }

    @Test
    @DisplayName("멤버 정보 변경")
    void updateProfile() {
        //given
        Member memberA = createMember("memberA");
        List<MemberProfile.History> careers = new ArrayList<>();
        careers.add(new MemberProfile.History("newCompany",
                LocalDate.of(2011, 2, 2),
                LocalDate.of(2022, 1, 1),
                "newPosition"));

        MemberProfile updateProfile = MemberProfile.builder()
                .email(memberA.getEmail())
                .username(memberA.getUsername())
                .nickname("newNickname")
                .careerList(careers).build();
        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(memberA));
        MemberProfile result = memberService.updateProfile(updateProfile);

        assertThat(result.getEmail()).isEqualTo(updateProfile.getEmail());
        assertThat(result.getUsername()).isEqualTo(updateProfile.getUsername());
        assertThat(result.getNickname()).isEqualTo(updateProfile.getNickname());
        assertThat(result.getCareerList().get(0).getCompanyName())
                .isEqualTo(updateProfile.getCareerList().get(0).getCompanyName());
        assertThat(result.getCareerList().get(0).getPosition())
                .isEqualTo(updateProfile.getCareerList().get(0).getPosition());

    }

    private Member createMember(String dummy) {

        Member member = Member.builder().email(dummy + "@email.com")
                .nickname(dummy + "Nickname")
                .username(dummy + "Name")
                .userPassword(dummy + "12QW!@")
                .oAuthId("FORM")
                .role(UserRole.MENTEE)
                .build();
        ArrayList<Career> careerList = new ArrayList<>();
        careerList.add(Career.builder()
                .member(member)
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.now())
                .position("testPosition")
                .companyName("testCompany")
                .build());
        for (Career career : careerList) {
            career.setMember(member);
        }
        return member;
    }



}
