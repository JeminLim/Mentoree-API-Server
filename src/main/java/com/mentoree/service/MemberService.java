package com.mentoree.service;

import com.mentoree.domain.entity.Career;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Mentee;
import com.mentoree.domain.entity.Mentor;
import com.mentoree.domain.repository.*;
import com.mentoree.exception.DuplicateDataException;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.MemberProfileDto;
import com.mentoree.service.dto.MemberSignUpRequestDto;
import com.mentoree.service.dto.MenteeDto;
import com.mentoree.service.dto.MentorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CareerRepository careerRepository;
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(MemberSignUpRequestDto signUpRequest) {
        if(emailCheck(signUpRequest.getEmail()) || nicknameCheck(signUpRequest.getNickname())) {
            throw new DuplicateDataException(Member.class, "Member already exist");
        }
        memberRepository.save(signUpRequest.toEntity("FORM", passwordEncoder));
    }

    @Transactional
    public MemberProfileDto getProfile(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        return MemberProfileDto.of(findMember);
    }

    @Transactional
    public MemberProfileDto updateProfile(MemberProfileDto updateProfile) {
        Member findMember = memberRepository.findById(updateProfile.getId()).orElseThrow(NoDataFoundException::new);
        updateMemberProfile(updateProfile, findMember);
        return MemberProfileDto.of(findMember);
    }

    @Transactional
    public void transformMember(MemberProfileDto memberProfileDto) {
        validationTransform(memberProfileDto);
        Member findMember = memberRepository.findById(memberProfileDto.getId()).orElseThrow(NoDataFoundException::new);
        updateMemberProfile(memberProfileDto, findMember);
        findMember.transformMentor();
    }

    @Transactional
    public void withdrawMember(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        findMember.requestWithdraw();
    }

    @Transactional
    public Map<String, Object> login(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        List<Mentor> participatedMentorList = mentorRepository.findAllByMemberId(memberId);
        List<Mentee> participatedMenteeList = menteeRepository.findAllParticipatedMentee(memberId);

        Map<String, Object> result = new HashMap<>();
        result.put("MemberProfile", MemberProfileDto.of(findMember));
        result.put("ParticipatedMentor", participatedMentorList.stream().map(MentorDto::of).collect(Collectors.toList()));
        result.put("ParticipatedMentee", participatedMenteeList.stream().map(MenteeDto::of).collect(Collectors.toList()));

        return result;
    }

    private void updateMemberProfile(MemberProfileDto updateProfile, Member findMember) {

        String updateNickname = updateProfile.getNickname();
        if(!findMember.getNickname().equals(updateNickname) && memberRepository.existsByNickname(updateNickname)) {
            throw new DuplicateDataException("이미 존재하는 닉네임 입니다.");
        }

        if(updateProfile.getUsername() != null && findMember.getUsername() != null && !findMember.getUsername().equals(updateProfile.getUsername())) {
            throw new IllegalStateException("이름은 두 번 이상 변경할 수 없습니다.");
        }

        findMember.updateNickname(updateNickname);
        findMember.updateUsername(updateProfile.getUsername());

        List<Career> careerList = updateProfile.getHistories().stream()
                .map(Career::new).collect(Collectors.toList());

        careerRepository.deleteAllByMember(findMember);
        findMember.getCareers().clear();

        for (Career career : careerList) {
            career.setMember(findMember);
        }
        careerRepository.saveAll(careerList);
    }



    public boolean duplicateEmailCheck(String email) {
        return emailCheck(email);
    }

    public boolean duplicateNicknameCheck(String nickname) {
        return nicknameCheck(nickname);
    }

    private boolean emailCheck(String email) {
        return memberRepository.existsByEmail(email);
    }

    private boolean nicknameCheck(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    private void validationTransform(MemberProfileDto memberProfileDto) {

        if(memberProfileDto.getUsername().isEmpty())
            throw new IllegalArgumentException("이름은 반드시 포함되어야 합니다.");

        if(memberProfileDto.getHistories().size() == 0)
            throw new IllegalArgumentException("반드시 경력이 존재해야 합니다.");

    }

}
