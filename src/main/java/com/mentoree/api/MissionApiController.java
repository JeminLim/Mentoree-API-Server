package com.mentoree.api;

import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.config.interceptors.Authority;
import com.mentoree.service.MissionService;
import com.mentoree.service.dto.MissionCreateRequestDto;
import com.mentoree.service.dto.MissionInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mentoree.config.interceptors.Authority.Role.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionApiController {

    private final MissionService missionService;

    @Authority(role = MENTOR)
    @PostMapping("/create")
    public ResponseEntity createMission(@RequestBody MissionCreateRequestDto createRequest) {
        Long memberId = getMemberId();
        missionService.create(memberId, createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Authority(role = WRITER)
    @PostMapping("/update/{missionId}")
    public ResponseEntity updateMission(@PathVariable("missionId") Long missionId,
                                        @RequestBody MissionCreateRequestDto updateRequest) {
        missionService.update(missionId, updateRequest);
        return ResponseEntity.ok().build();
    }
    @Authority(role = WRITER)
    @DeleteMapping("/{missionId}")
    public ResponseEntity deleteMission(@PathVariable("missionId") Long missionId) {
        missionService.delete(missionId);
        return ResponseEntity.ok().build();
    }

    @Authority(role = PARTICIPANT)
    @GetMapping("/{missionId}")
    public ResponseEntity getMissionInfo(@PathVariable("missionId") Long missionId) {
        MissionInfoDto result = missionService.getMissionInfo(missionId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("mission", result);
        return ResponseEntity.ok().body(responseBody);
    }
    @Authority(role = PARTICIPANT)
    @GetMapping("/list/{programId}")
    public ResponseEntity getMissionInfoList(@PathVariable("programId") Long programId
                                            , @RequestParam(required = false, defaultValue = "false") Boolean expiration) {
        List<MissionInfoDto> result = missionService.getMissionInfoList(programId, expiration);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("missionList", result);
        return ResponseEntity.ok().body(responseBody);
    }

    private Long getMemberId() {
        AuthenticateUser principal = (AuthenticateUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }

}
