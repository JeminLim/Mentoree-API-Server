package com.mentoree.service;

import com.mentoree.domain.entity.Board;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Reply;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.ReplyRepository;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.ReplyCreateRequestDto;
import com.mentoree.service.dto.ReplyInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public ReplyInfoDto create(Long memberId, ReplyCreateRequestDto createRequest) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        Board findBoard = boardRepository.findById(createRequest.getBoardId()).orElseThrow(NoDataFoundException::new);

        Reply savedReply = replyRepository.save(createRequest.toEntity(findBoard, findMember));
        return ReplyInfoDto.of(savedReply);
    }

    @Transactional
    public ReplyInfoDto update(Long replyId, String content) {
        Reply findReply = replyRepository.findById(replyId).orElseThrow(NoDataFoundException::new);
        findReply.updateContent(content);
        return ReplyInfoDto.of(findReply);
    }

    @Transactional
    public void remove(Long replyId) {
        Reply findReply = replyRepository.findById(replyId).orElseThrow(NoDataFoundException::new);
        findReply.remove();
    }

    @Transactional(readOnly = true)
    public List<ReplyInfoDto> getReplyList(Long boardId) {
        List<Reply> findResult = replyRepository.findAllByBoardId(boardId);
        return findResult.stream().map(ReplyInfoDto::of).collect(Collectors.toList());
    }

}
