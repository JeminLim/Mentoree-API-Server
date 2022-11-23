package com.mentoree.service;

import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.CategoryRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.ProgramRepository;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.MentorDto;
import com.mentoree.service.dto.ProgramCreateRequestDto;
import com.mentoree.service.dto.ProgramInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final MentorRepository mentorRepository;

    @Transactional
    public MentorDto createProgram(Long memberId, ProgramCreateRequestDto request) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        Category category = categoryRepository
                .findByCategoryName(request.getCategoryName()).orElseThrow(NoDataFoundException::new);
        Program saved = programRepository.save(request.toEntity(category));
        Mentor mentor = Mentor.builder()
                .host(true)
                .member(loginMember)
                .build();
        mentor.setProgram(saved);
        mentorRepository.save(mentor);

        return MentorDto.of(mentor);
    }

    @Transactional
    public ProgramInfoDto updateProgram(Long programId, ProgramCreateRequestDto updateRequest) {
        // find program
        Program findProgram = programRepository.findById(programId).orElseThrow(NoDataFoundException::new);
        Category category = categoryRepository.findByCategoryName(updateRequest.getCategoryName())
                .orElseThrow(NoDataFoundException::new);
        // update program info
        findProgram.update(updateRequest.getProgramName(), updateRequest.getDescription(),
                        updateRequest.getMaxMember(), updateRequest.getPrice(), category, updateRequest.getDueDate());
        // convert to info dto and return
        return ProgramInfoDto.of(findProgram);
    }

}
