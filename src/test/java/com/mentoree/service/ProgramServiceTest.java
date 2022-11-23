package com.mentoree.service;


import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.CategoryRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.ProgramRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private MentorRepository mentorRepository;

    @InjectMocks
    private ProgramService programService;

    @Test
    @DisplayName("프로그램 생성 요청")
    void createProgramTest() {

        //given
        Category fDepthCat = genFirstCategory("Programming");
        Category sDepthCat = genSecondCategory("JAVA", fDepthCat);
        Member host = genMember("memberA", UserRole.MENTOR);
        Program program = genProgram("test", sDepthCat);
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
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.of(sDepthCat));

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
        Category fDepthCat = genFirstCategory("Programming");
        Category sDepthCat = genSecondCategory("JAVA", fDepthCat);
        Program oldProgram = genProgram("oldProgram", sDepthCat);
        ProgramCreateRequestDto request = ProgramCreateRequestDto.builder()
                .programName("newProgram")
                .maxMember(5)
                .price(15000)
                .dueDate(LocalDate.now().plusDays(5))
                .categoryName("JAVA")
                .description("changed description")
                .build();

        when(programRepository.findById(any())).thenReturn(Optional.of(oldProgram));
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.of(sDepthCat));

        //when
        ProgramInfoDto result = programService.updateProgram(1L, request);

        assertThat(result.getProgramName()).isEqualTo(request.getProgramName());
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
        assertThat(result.getDescription()).isEqualTo(request.getDescription());
    }

    private Program genProgram(String dummy, Category category) {
        return Program.builder()
                .programName(dummy + "Program")
                .description(dummy + " program description")
                .price(10000)
                .dueDate(LocalDate.now().plusDays(7))
                .category(category)
                .maxMember(5)
                .build();
    }

    private Category genFirstCategory(String categoryName) {
        return Category.builder()
                .categoryName(categoryName)
                .build();
    }

    private Category genSecondCategory(String categoryName, Category parent) {
        Category cat = Category.builder().categoryName(categoryName).build();
        cat.setParent(parent);
        return cat;
    }

    private Member genMember(String dummy, UserRole role) {
        return Member.builder()
                .email(dummy + "@email.com")
                .nickname(dummy + "nickname")
                .username(dummy)
                .oAuthId("FORM")
                .role(role)
                .userPassword("1234Qwer!@")
                .build();
    }

    private Mentor genMentor(Member member, Program program, boolean host) {
        return Mentor.builder()
                .program(program)
                .member(member)
                .host(host)
                .build();
    }



}
