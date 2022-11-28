package com.mentoree.service;

import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.*;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final int PAGE_SIZE = 8;
    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;
    private final CategoryRepository categoryRepository;
    private final MentorRepository mentorRepository;
    private final ApplicantRepository applicantRepository;
    private final MenteeRepository menteeRepository;

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

        Program program = applicant.getProgram();
        Member member = applicant.getMember();

        if(applicant.getRole().equals(ProgramRole.MENTEE)){
            Mentee newMentee = Mentee.builder().member(member).build();
            newMentee.acceptProgram(program);
            program.incrementMentee();
            menteeRepository.save(newMentee);
        }

        if(applicant.getRole().equals(ProgramRole.MENTOR)){
            Mentor newMentor = Mentor.builder().member(member).build();
            newMentor.setProgram(program);
            mentorRepository.save(newMentor);
        }
    }

    @Transactional
    public void rejectApplicant(Long applicantId) {
        applicantRepository.deleteById(applicantId);
    }

    @Transactional(readOnly = true)
    public ProgramInfoDto getProgramInfo(Long programId) {
        Program program = programRepository.findById(programId).orElseThrow(NoDataFoundException::new);
        return ProgramInfoDto.of(program);
    }

    @Transactional
    public void withdrawProgram(Long programId) {
        Program program = programRepository.findById(programId).orElseThrow(NoDataFoundException::new);
        program.withdraw();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProgramList(Long minId, Long maxId, String first, List<String> second) {

        Map<String, Object> data = new HashMap<>();

        List<Program> recentProgramList = programRepository.getRecentProgramList(maxId, first, second).getContent();

        if(recentProgramList.size() < PAGE_SIZE) {
            if(minId == null)
                minId = recentProgramList.get(0).getId();

            PageRequest page = PageRequest.of(0, PAGE_SIZE - recentProgramList.size());
            Slice<Program> programList = programRepository.getProgramList(minId, first, second, page);

            List<ProgramInfoDto> result = Stream.concat(recentProgramList.stream(), programList.getContent().stream())
                    .map(ProgramInfoDto::of).collect(Collectors.toList());

            data.put("programList", result);
            data.put("next", programList.hasNext());
            return data;
        }

        List<ProgramInfoDto> result = recentProgramList.stream().map(ProgramInfoDto::of).collect(Collectors.toList());
        data.put("programList", result);
        data.put("next", true);

        return data;
    }

}
