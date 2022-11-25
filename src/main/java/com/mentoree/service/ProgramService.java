package com.mentoree.service;

import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.*;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final MentorRepository mentorRepository;
    private final ApplicantRepository applicantRepository;

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

    @Transactional
    public void applyProgram(Long memberId, ProgramApplyDto applyRequest) {
        Member loginMember = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        Program findProgram = programRepository.findById(applyRequest.getProgramId()).orElseThrow(NoDataFoundException::new);
        Applicant applicant = applyRequest.toEntity(loginMember, findProgram);
        applicantRepository.save(applicant);
    }

    @Transactional(readOnly = true)
    public List<ApplicantDto> getApplicantList(Long programId) {
        List<Applicant> applicantList = applicantRepository.findAllByProgramId(programId);
        return applicantList.stream().map(ApplicantDto::of).collect(Collectors.toList());
    }

    @Transactional
    public void acceptApplicant(Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(NoDataFoundException::new);
        applicant.approve();
    }

    @Transactional
    public void rejectApplicant(Long applicantId) {
        applicantRepository.deleteById(applicantId);
    }

}
