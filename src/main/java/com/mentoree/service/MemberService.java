package com.mentoree.service;

import com.mentoree.domain.entity.Member;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.exception.DuplicateDataException;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.MemberProfileDto;
import com.mentoree.service.dto.MemberSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void signUp(MemberSignUpRequestDto signUpRequest) {
        if(emailCheck(signUpRequest.getEmail()) || nicknameCheck(signUpRequest.getNickname())) {
            throw new DuplicateDataException(Member.class, "Member already exist");
        }
        memberRepository.save(signUpRequest.toEntity("FORM"));
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

    private void updateMemberProfile(MemberProfileDto updateProfile, Member findMember) {
        findMember.updateNickname(updateProfile.getNickname());
        findMember.updateCareer(updateProfile.getHistories());
        findMember.updateUsername(updateProfile.getUsername());
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
