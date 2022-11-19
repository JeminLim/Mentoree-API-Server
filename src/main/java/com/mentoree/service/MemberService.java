package com.mentoree.service;

import com.mentoree.domain.entity.Member;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.exception.DuplicateDataException;
import com.mentoree.service.dto.MemberSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void signUp(MemberSignUpRequest signUpRequest) {
        if(emailCheck(signUpRequest.getEmail()) || nicknameCheck(signUpRequest.getNickname())) {
            throw new DuplicateDataException(Member.class, "Member already exist");
        }
        memberRepository.save(signUpRequest.toEntity("FORM"));
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

}
