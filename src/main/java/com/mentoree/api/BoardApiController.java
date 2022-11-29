package com.mentoree.api;

import com.mentoree.service.BoardService;
import com.mentoree.service.dto.BoardCreateRequestDto;
import com.mentoree.service.dto.BoardInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {

    private final BoardService boardService;

    @PostMapping("/create")
    public ResponseEntity createBoard(@RequestBody BoardCreateRequestDto createRequest) {
        // Authority check
        //Authentication member id 추출
        Long memberId = 1L;
        boardService.create(memberId, createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update/{boardId}")
    public ResponseEntity updateBoard(@PathVariable("boardId") Long boardId,
                                        @RequestBody BoardCreateRequestDto updateRequest) {

        //Authority check

        boardService.update(boardId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity deleteMission(@PathVariable("boardId") Long boardId) {
        //Authority check
        boardService.delete(boardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity getBoardInfo(@PathVariable("boardId") Long boardId) {
        //Authority check

        BoardInfoDto result = boardService.getBoardInfo(boardId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("board", result);
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/list/{missionId}")
    public ResponseEntity getBoardInfoList(@PathVariable("missionId") Long missionId) {
        //Authority check

        List<BoardInfoDto> result = boardService.getBoardInfoList(missionId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("boardList", result);
        return ResponseEntity.ok().body(responseBody);


    }

}
