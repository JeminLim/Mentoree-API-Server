package com.mentoree.api;


import com.mentoree.service.MemberService;
import com.mentoree.service.dto.MemberProfileDto;
import com.mentoree.service.dto.MemberSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/members")
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/join/check/email")
    public ResponseEntity checkEmail(@RequestParam String email) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("result", memberService.duplicateEmailCheck(email));
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/join/check/nickname")
    public ResponseEntity checkNickname(@RequestParam String nickname) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("result", memberService.duplicateNicknameCheck(nickname));
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/join")
    public ResponseEntity signUp(@RequestBody MemberSignUpRequestDto signUpForm) {
        memberService.signUp(signUpForm);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity getProfile(@PathVariable("id") Long memberId) {
        MemberProfileDto member = memberService.getProfile(memberId);
        Map<String, Object> result = new HashMap<>();
        result.put("member", member);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/profiles")
    public ResponseEntity updateProfile(@RequestBody MemberProfileDto updateProfile) {
        MemberProfileDto updateResult = memberService.updateProfile(updateProfile);
        Map<String, Object> result = new HashMap<>();
        result.put("member", updateResult);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/transform")
    public ResponseEntity transformToMentor(@RequestBody MemberProfileDto memberProfileDto) {
        memberService.transformMember(memberProfileDto);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/withdraw/{id}")
    public ResponseEntity memberWithdraw(@PathVariable("id") Long memberId) {
        memberService.withdrawMember(memberId);
        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        return ResponseEntity.ok().body(result);
    }

}
