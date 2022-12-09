package com.mentoree.api;

import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.config.interceptors.Authority;
import com.mentoree.service.ProgramService;
import com.mentoree.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mentoree.config.interceptors.Authority.Role.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programs")
public class ProgramApiController {

    private final ProgramService programService;

    @PostMapping("/create")
    public ResponseEntity createProgram(@RequestBody ProgramCreateRequestDto createRequest) {
        validateUserRole();
        Long memberId = getLoginMemberId();

        MentorDto responseBody = programService.createProgram(memberId, createRequest);
        Map<String, Object> result = new HashMap<>();
        result.put("mentor", responseBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Authority(role = HOST)
    @PostMapping("/update/{programId}")
    public ResponseEntity updateProgram(@PathVariable("programId") Long programId,
                                        @RequestBody ProgramCreateRequestDto updateRequest) {
        ProgramInfoDto result = programService.updateProgram(programId, updateRequest);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("program", result);

        return ResponseEntity.ok().body(responseBody);
    }

    @PostMapping("/apply")
    public ResponseEntity applyProgram(@RequestBody ProgramApplyDto applyRequest) {

        Long loginMember = getLoginMemberId();
        programService.applyProgram(loginMember, applyRequest);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", "success");

        return ResponseEntity.ok().body(responseBody);
    }

    @Authority(role = HOST)
    @GetMapping("/applicants/{programId}")
    public ResponseEntity manageProgram(@PathVariable("programId") Long programId) {
        List<ApplicantDto> applicantList = programService.getApplicantList(programId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("applicants", applicantList);
        return ResponseEntity.ok().body(responseBody);
    }

    @Authority(role = HOST)
    @PostMapping("/{programId}/applicants/accept/{applicantId}")
    public ResponseEntity acceptApplicant(@PathVariable("programId") Long programId,
                                          @PathVariable("applicantId") Long applicantId) {
        programService.acceptApplicant(applicantId);
        return ResponseEntity.ok().build();
    }

    @Authority(role = HOST)
    @DeleteMapping("/{programId}/applicants/reject/{applicantId}")
    public ResponseEntity rejectApplicant(@PathVariable("programId") Long programId,
                                          @PathVariable("applicantId") Long applicantId) {
        programService.rejectApplicant(applicantId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{programId}")
    public ResponseEntity getProgramInfo(@PathVariable("programId") Long programId) {
        ProgramInfoDto programInfo = programService.getProgramInfo(programId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("program", programInfo);
        return ResponseEntity.ok().body(responseBody);
    }

    @Authority(role = HOST)
    @PostMapping("/withdraw/{programId}")
    public ResponseEntity withdrawProgram(@PathVariable("programId") Long programId) {
        programService.withdrawProgram(programId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity getProgramList(@RequestParam(value = "maxId", required = false) Long maxId,
                                        @RequestParam(value = "minId", required = false) Long minId,
                                        @RequestParam(value = "first", required = false) String firstCategory,
                                        @RequestParam(value = "second", required = false) String secondCategory) {
        List<String> secondCategories = secondCategory == null ? null : List.of(secondCategory.split(","));
        Map<String, Object> responseBody = programService.getProgramList(minId, maxId, firstCategory, secondCategories);
        return ResponseEntity.ok().body(responseBody);
    }

    private void validateUserRole() {
        AuthenticateUser principal
                = (AuthenticateUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String authority = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        if(!authority.equals("ROLE_MENTOR"))
            throw new IllegalStateException("권한이 없는 유저입니다.");
    }

    private Long getLoginMemberId() {
        AuthenticateUser principal
                = (AuthenticateUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }
}
