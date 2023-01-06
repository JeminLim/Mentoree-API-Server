package com.mentoree.api;

import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.config.interceptors.Authority;
import com.mentoree.service.BoardService;
import com.mentoree.service.dto.BoardCreateRequestDto;
import com.mentoree.service.dto.BoardInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mentoree.config.interceptors.Authority.Role.PARTICIPANT;
import static com.mentoree.config.interceptors.Authority.Role.WRITER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {

    private final BoardService boardService;
    @Authority(role = PARTICIPANT)
    @PostMapping("/create")
    public ResponseEntity createBoard(@RequestBody BoardCreateRequestDto createRequest) {
        Long memberId = getMemberId();
        Long boardId = boardService.create(memberId, createRequest, false);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("boardId", boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Authority(role = PARTICIPANT)
    @PostMapping("/create/temp")
    public ResponseEntity temporalSave(@RequestBody BoardCreateRequestDto createRequest) {
        Long memberId = getMemberId();
        Long boardId = boardService.create(memberId, createRequest, true);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("boardId", boardId);
        return ResponseEntity.ok().body(responseBody);
    }

    @Authority(role = WRITER)
    @PostMapping("/update/{boardId}")
    public ResponseEntity updateBoard(@PathVariable("boardId") Long boardId,
                                        @RequestBody BoardCreateRequestDto updateRequest) {
        boardService.update(boardId, updateRequest);
        return ResponseEntity.ok().build();
    }
    @Authority(role = WRITER)
    @DeleteMapping("/{boardId}")
    public ResponseEntity deleteMission(@PathVariable("boardId") Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok().build();
    }
    @Authority(role = PARTICIPANT)
    @GetMapping("/{boardId}")
    public ResponseEntity getBoardInfo(@PathVariable("boardId") Long boardId) {
        BoardInfoDto result = boardService.getBoardInfo(boardId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("board", result);
        return ResponseEntity.ok().body(responseBody);
    }
    @Authority(role = PARTICIPANT)
    @GetMapping("/list/{missionId}")
    public ResponseEntity getBoardInfoList(@PathVariable("missionId") Long missionId) {
        List<BoardInfoDto> result = boardService.getBoardInfoList(missionId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("boardList", result);
        return ResponseEntity.ok().body(responseBody);
    }

    @Authority(role = PARTICIPANT)
    @PostMapping(value = "{boardId}/images")
    public ResponseEntity uploadImage(@PathVariable("boardId") Long boardId,
                                      @RequestParam("image") MultipartFile file) {

        String savedPath = boardService.uploadImages(boardId, file);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("path", savedPath);
        responseBody.put("filename", file.getOriginalFilename());
        return ResponseEntity.ok().body(responseBody);
    }

    @Authority(role = PARTICIPANT)
    @GetMapping("/{missionId}/temp")
    public ResponseEntity getTempBoardWriting(@PathVariable("missionId") Long missionId) {
        Long loginMemberId = getMemberId();
        Optional<BoardInfoDto> temporalWriting = boardService.getTemporalWriting(loginMemberId, missionId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("writingBoard", temporalWriting);
        return ResponseEntity.ok().body(responseBody);
    }


    private Long getMemberId() {
        AuthenticateUser principal = (AuthenticateUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }
}
