package com.mentoree.service;

import com.mentoree.domain.entity.Board;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Mission;
import com.mentoree.domain.entity.Writing;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.MissionRepository;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.BoardCreateRequestDto;
import com.mentoree.service.dto.BoardInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void create(Long memberId, BoardCreateRequestDto request) {
        Member writer = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(NoDataFoundException::new);
        Board newBoard = request.toEntity(mission, writer);
        boardRepository.save(newBoard);
    }

    @Transactional
    public void update(Long boardId, BoardCreateRequestDto updateRequest) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(NoDataFoundException::new);
        Writing updateWriting = new Writing(updateRequest.getTitle(), updateRequest.getDescription());
        findBoard.update(updateWriting);
    }

    @Transactional
    public void delete(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    @Transactional(readOnly = true)
    public BoardInfoDto getBoardInfo(Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(NoDataFoundException::new);
        return BoardInfoDto.of(findBoard);
    }

    @Transactional(readOnly = true)
    public List<BoardInfoDto> getBoardInfoList(Long missionId) {
        List<Board> findResult = boardRepository.findAllByMissionId(missionId);
        return findResult.stream().map(BoardInfoDto::of).collect(Collectors.toList());
    }

}
