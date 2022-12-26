package com.mentoree.service;

import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.*;
import com.mentoree.exception.DuplicateDataException;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

        Optional<Applicant> findApplicant = applicantRepository.findByMemberAndProgram(loginMember, findProgram);
        if(!findApplicant.isEmpty()) {
            throw new DuplicateDataException(Applicant.class, "이미 신청한 프로그램 입니다.");
        }
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
        Program program = applicant.getProgram();
        if(program.getMentee().size() >= program.getMaxMember()){
            throw new IllegalStateException("정원 초과 입니다.");
        }

        applicant.approve();
        Member member = applicant.getMember();

        if(applicant.getRole().equals(ProgramRole.MENTEE)){
            Mentee newMentee = Mentee.builder().member(member).build();
            newMentee.acceptProgram(program);
            // 임시
            newMentee.paid();
            program.jointMentee();
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
        Slice<Program> recentProgramList = programRepository.getRecentProgramList(maxId, first, second);
        Map<String, Object> data = supplementProgramList(minId, first, second, recentProgramList);
        return data;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCategoryList() {
        Map<String, Object> result = new HashMap<>();
        List<Category> allCategoryList = categoryRepository.findAll();
        List<CategoryDto> firstCategory
                = allCategoryList.stream().filter(c -> c.getParent() == null)
                .map(CategoryDto::of).collect(Collectors.toList());

        List<CategoryDto> secondCategory
                = allCategoryList.stream().filter(c -> c.getParent() != null)
                .map(CategoryDto::of).collect(Collectors.toList());

        result.put("firstCategories", firstCategory);
        result.put("secondCategories", secondCategory);
        return result;
    }

    private Map<String, Object> supplementProgramList(Long minId, String first, List<String> second, Slice<Program> programListSlice) {
        List<Program> programList = programListSlice.getContent().isEmpty() ? new ArrayList<>() : programListSlice.getContent();
        boolean hasNext = programListSlice.hasNext();

        if(programList.size() < PAGE_SIZE) {
            if(minId == 0 && programList.size() > 0) {
                minId = programList.get(programList.size() - 1).getId();
            }

            PageRequest page = PageRequest.of(0, PAGE_SIZE - programList.size());
            Slice<Program> supplementList = programRepository.getProgramList(minId, first, second, page);

            if(!supplementList.isEmpty()) {
                programList.addAll(supplementList.getContent());
                hasNext = supplementList.hasNext();
            }
        }

        List<ProgramInfoDto> resultList = programList.stream().map(ProgramInfoDto::of).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("programList", resultList);
        result.put("next", hasNext);
        return result;
    }
}
