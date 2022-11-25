package com.mentoree.api;

import com.mentoree.service.ProgramService;
import com.mentoree.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programs")
public class ProgramApiController {

    private final ProgramService programService;

    @PostMapping("/create")
    public ResponseEntity createProgram(@RequestBody ProgramCreateRequestDto createRequest) {
        // Authority check needed
        Long memberId = 1L; // security 적용 후, 로그인 정보 추추

        MentorDto responseBody = programService.createProgram(memberId, createRequest);
        Map<String, Object> result = new HashMap<>();
        result.put("mentor", responseBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity updateProgram(@PathVariable("id") Long programId,
                                        @RequestBody ProgramCreateRequestDto updateRequest) {

        //Authority check needed


        ProgramInfoDto result = programService.updateProgram(programId, updateRequest);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("program", result);

        return ResponseEntity.ok().body(responseBody);
    }

    @PostMapping("/apply")
    public ResponseEntity applyProgram(@RequestBody ProgramApplyDto applyRequest) {

        //Extract login info from authentication
        Long loginMember = 2L;

        programService.applyProgram(loginMember, applyRequest);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", "success");

        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/applicants/{id}")
    public ResponseEntity manageProgram(@PathVariable("id") Long programId) {
        //authority check
        List<ApplicantDto> applicantList = programService.getApplicantList(programId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("applicants", applicantList);
        return ResponseEntity.ok().body(responseBody);
    }

    @PostMapping("/applicants/accept/{applicantId}")
    public ResponseEntity acceptApplicant(@PathVariable("applicantId") Long applicantId) {
        //authority check
        programService.acceptApplicant(applicantId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/applicants/reject/{applicantId}")
    public ResponseEntity rejectApplicant(@PathVariable("applicantId") Long applicantId) {
        //authority check
        programService.rejectApplicant(applicantId);
        return ResponseEntity.ok().build();
    }

}
