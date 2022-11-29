package com.mentoree.api;

import com.mentoree.service.MissionService;
import com.mentoree.service.dto.MissionCreateRequestDto;
import com.mentoree.service.dto.MissionInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionApiController {

    private final MissionService missionService;


    @PostMapping("/create")
    public ResponseEntity createMission(@RequestBody MissionCreateRequestDto createRequest) {
        //Authentication member id 추출
        Long memberId = 1L;
        missionService.create(memberId, createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update/{missionId}")
    public ResponseEntity updateMission(@PathVariable("missionId") Long missionId,
                                        @RequestBody MissionCreateRequestDto updateRequest) {

        // Authority check

        missionService.update(missionId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{missionId}")
    public ResponseEntity deleteMission(@PathVariable("missionId") Long missionId) {
        //Authority check
        missionService.delete(missionId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{missionId}")
    public ResponseEntity getMissionInfo(@PathVariable("missionId") Long missionId) {
        //Authority check

        MissionInfoDto result = missionService.getMissionInfo(missionId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mission", result);
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/list/{programId}")
    public ResponseEntity getMissionInfoList(@PathVariable("programId") Long prgramId) {
        //Authority check


        List<MissionInfoDto> result = missionService.getMissionInfoList(prgramId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("missionList", result);
        return ResponseEntity.ok().body(responseBody);
    }

}
