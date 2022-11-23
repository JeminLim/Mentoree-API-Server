package com.mentoree.service;

import com.mentoree.domain.entity.History;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.UserRole;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.service.dto.MemberProfileDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
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

        MemberProfileDto findMember = memberService.getProfile(1L);

        assertThat(findMember.getEmail()).isEqualTo(memberA.getEmail());
        assertThat(findMember.getNickname()).isEqualTo(memberA.getNickname());
        assertThat(findMember.getUsername()).isEqualTo(memberA.getUsername());
        assertThat(findMember.getHistories().size()).isEqualTo(memberA.getCareers().size());
        assertThat(findMember.getHistories().get(0).getCompanyName())
                .isEqualTo(memberA.getCareers().get(0).getHistory().getCompanyName());
    }

    @Test
    @DisplayName("멤버 정보 변경")
    void updateProfile() {
        //given
        Member memberA = createMember("memberA");

        List<History> histories = new ArrayList<>();
        histories.add(new History("newCompany",
                LocalDate.of(2011, 2, 2),
                LocalDate.of(2022, 1, 1),
                "newPosition"));
        MemberProfileDto updateProfile = MemberProfileDto.builder()
                .email(memberA.getEmail())
                .username(memberA.getUsername())
                .nickname("newNickname")
                .histories(histories).build();
        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(memberA));
        MemberProfileDto result = memberService.updateProfile(updateProfile);

        assertThat(result.getEmail()).isEqualTo(updateProfile.getEmail());
        assertThat(result.getUsername()).isEqualTo(updateProfile.getUsername());
        assertThat(result.getNickname()).isEqualTo(updateProfile.getNickname());
        assertThat(result.getHistories().get(0).getCompanyName())
                .isEqualTo(updateProfile.getHistories().get(0).getCompanyName());
        assertThat(result.getHistories().get(0).getPosition())
                .isEqualTo(updateProfile.getHistories().get(0).getPosition());

    }


    @Test
    @DisplayName("멘토 회원 전환")
    void transformMemberTest() {

        Member memberA = createMember("memberA");
        MemberProfileDto memberProfileDto = MemberProfileDto.of(memberA);

        when(memberRepository.findById(any())).thenReturn(Optional.of(memberA));
        memberService.transformMember(memberProfileDto);

        assertThat(memberA.getRole()).isEqualTo(UserRole.MENTOR);

    }

    @Test
    @DisplayName("회원 탈퇴 요청")
    void withdrawTest() {
        Member memberA = createMember("memberA");

        when(memberRepository.findById(any())).thenReturn(Optional.of(memberA));
        memberService.withdrawMember(1L);

        assertThat(memberA.getWithdrawal()).isTrue();

    }

    private Member createMember(String dummy) {

        Member member = Member.builder().email(dummy + "@email.com")
                .nickname(dummy + "Nickname")
                .username(dummy + "Name")
                .userPassword(dummy + "12QW!@")
                .oAuthId("FORM")
                .role(UserRole.MENTEE)
                .build();
        List<History> histories = new ArrayList<>();
        histories.add(new History("oldCompany",
                LocalDate.of(2011,1,1),
                LocalDate.of(2022,1,1),
                "oldPosition"));
        member.updateCareer(histories);
        return member;
    }




}
