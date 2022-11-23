package com.mentoree.api;

import com.mentoree.service.ProgramService;
import com.mentoree.service.dto.MentorDto;
import com.mentoree.service.dto.ProgramCreateRequestDto;
import com.mentoree.service.dto.ProgramInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


}
