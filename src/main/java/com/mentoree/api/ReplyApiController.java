package com.mentoree.api;

import com.mentoree.service.ReplyService;
import com.mentoree.service.dto.ReplyCreateRequestDto;
import com.mentoree.service.dto.ReplyInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyApiController {

    private final ReplyService replyService;

    @PostMapping("/create")
    public ResponseEntity createReply(@RequestBody ReplyCreateRequestDto createRequest) {

        //Authority check

        //from authentication
        Long memberId = 1L;

        ReplyInfoDto saveResult = replyService.create(memberId, createRequest);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("reply", saveResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PatchMapping("/update/{replyId}")
    public ResponseEntity updateReply(@PathVariable("replyId") Long replyId,
                                      @RequestBody Map<String, String> content) {
        //Authority Check

        ReplyInfoDto updateResult = replyService.update(replyId, content.get("content"));
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("reply", updateResult);
        return ResponseEntity.ok().body(responseBody);
    }

    @PostMapping("/remove/{replyId}")
    public ResponseEntity removeReply(@PathVariable("replyId") Long replyId) {
        //Authority check

        replyService.remove(replyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/{boardId}")
    public ResponseEntity getReplyList(@PathVariable("boardId") Long boardId) {
        //Authority check

        List<ReplyInfoDto> replyList = replyService.getReplyList(boardId);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("replyList", replyList);
        return ResponseEntity.ok().body(responseBody);
    }

}
