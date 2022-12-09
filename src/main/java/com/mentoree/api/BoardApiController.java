package com.mentoree.api;

import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.config.interceptors.Authority;
import com.mentoree.service.BoardService;
import com.mentoree.service.dto.BoardCreateRequestDto;
import com.mentoree.service.dto.BoardInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mentoree.config.interceptors.Authority.Role.PARTICIPANT;
import static com.mentoree.config.interceptors.Authority.Role.WRITER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {

    private final BoardService boardService;
    @Authority(role = PARTICIPANT)
    @PostMapping("/create")
    public ResponseEntity createBoard(@RequestBody BoardCreateRequestDto createRequest) {
        Long memberId = getMemberId();
        boardService.create(memberId, createRequest);
        System.out.println("find as now");
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    private Long getMemberId() {
        AuthenticateUser principal = (AuthenticateUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
    }
}
