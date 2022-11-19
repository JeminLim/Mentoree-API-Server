package com.mentoree.api;


import com.mentoree.service.MemberService;
import com.mentoree.service.dto.MemberSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/members")
@RequiredArgsConstructor
@RestController
public class MemberAPIController {

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
    public ResponseEntity signUp(@RequestBody MemberSignUpRequest signUpForm) {
        memberService.signUp(signUpForm);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
