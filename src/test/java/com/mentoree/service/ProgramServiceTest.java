package com.mentoree.service;


import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.*;
import com.mentoree.generator.DummyDataBuilder;
import com.mentoree.service.dto.ApplicantDto;
import com.mentoree.service.dto.MentorDto;
import com.mentoree.service.dto.ProgramCreateRequestDto;
import com.mentoree.service.dto.ProgramInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {

    DummyDataBuilder builder = new DummyDataBuilder();

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private MenteeRepository menteeRepository;

    @InjectMocks
    private ProgramService programService;

    @Test
    @DisplayName("프로그램 생성 요청")
    void createProgramTest() {

        //given
        Category category = builder.generateCategory("Programming", "JAVA");
        Member host = builder.generateMember("memberA", UserRole.MENTOR);
        Program program = builder.generateProgram("test", category);
        ProgramCreateRequestDto request = ProgramCreateRequestDto.builder()
                .programName("testProgram")
                .maxMember(5)
                .price(10000)
                .dueDate(LocalDate.now().plusDays(5))
                .categoryName("JAVA")
                .description("test program description")
                .build();
        ReflectionTestUtils.setField(host, "id", 1L);
        ReflectionTestUtils.setField(program, "id", 1L);

        when(programRepository.save(any())).thenReturn(program);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(host));
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.of(category));

        //when
        MentorDto result = programService.createProgram(1L, request);
        //then
        assertThat(result.getProgramName()).isEqualTo(request.getProgramName());
        assertThat(result.getUsername()).isEqualTo(host.getUsername());
        assertThat(result.getHost()).isTrue();
        verify(programRepository, times(1)).save(any(Program.class));
        verify(mentorRepository, times(1)).save(any(Mentor.class));
    }

    @Test
    @DisplayName("프로그램 업데이트 테스트")
    void updateProgramTest() {
        //given
        Category category = builder.generateCategory("Programming", "JAVA");
        Program oldProgram = builder.generateProgram("oldProgram", category);
        ProgramCreateRequestDto request = ProgramCreateRequestDto.builder()
                .programName("newProgram")
                .maxMember(5)
                .price(15000)
                .dueDate(LocalDate.now().plusDays(5))
                .categoryName("JAVA")
                .description("changed description")
                .build();

        when(programRepository.findById(any())).thenReturn(Optional.of(oldProgram));
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.of(category));

        //when
        ProgramInfoDto result = programService.updateProgram(1L, request);

        assertThat(result.getProgramName()).isEqualTo(request.getProgramName());
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @DisplayName("프로그램 참가 신청자 목록")
    void getApplicantsList() {
        Category category = builder.generateCategory("Programming", "JAVA");
        Member member = builder.generateMember("memberA", UserRole.MENTEE);
        Program program = builder.generateProgram("test", category);
        Applicant applicant = builder.generateApplicant(member, program, ProgramRole.MENTEE);

        when(applicantRepository.findAllByProgramId(anyLong())).thenReturn(List.of(applicant));

        List<ApplicantDto> result = programService.getApplicantList(1L);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNickname()).isEqualTo(member.getNickname());
        assertThat(result.get(0).getRole()).isEqualTo(ProgramRole.MENTEE.getValue());
    }

    @Test
    @DisplayName("참가자 신청 승인")
    void acceptApplicant() {
        Member member = builder.generateMember("memberA", UserRole.MENTEE);
        Program program = builder.generateProgram("test", builder.generateCategory("Programming", "JAVA"));
        Applicant applicant = builder.generateApplicant(member, program, ProgramRole.MENTEE);

        when(applicantRepository.findById(anyLong())).thenReturn(Optional.of(applicant));

        programService.acceptApplicant(1L);

        verify(menteeRepository, times(1)).save(any(Mentee.class));
    }
}
