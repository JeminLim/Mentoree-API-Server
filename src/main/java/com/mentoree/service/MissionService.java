package com.mentoree.service;

import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MemberRepository;
import com.mentoree.domain.repository.MissionRepository;
import com.mentoree.domain.repository.ProgramRepository;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.dto.MissionCreateRequestDto;
import com.mentoree.service.dto.MissionInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    private final ProgramRepository programRepository;
    private final BoardRepository boardRepository;


    @Transactional
    public void create(Long memberId, MissionCreateRequestDto request) {
        Member writer = memberRepository.findById(memberId).orElseThrow(NoDataFoundException::new);
        Program program = programRepository.findById(request.getProgramId()).orElseThrow(NoDataFoundException::new);
        Mission newMission = request.toEntity(program, writer);
        missionRepository.save(newMission);
    }

    @Transactional
    public void update(Long missionId, MissionCreateRequestDto updateRequest) {
        Mission findMission = missionRepository.findById(missionId).orElseThrow(NoDataFoundException::new);
        Writing updateWriting = new Writing(updateRequest.getTitle(), updateRequest.getDescription());
        findMission.update(updateWriting, updateRequest.getDueDate());
    }

    @Transactional
    public void delete(Long missionId) {
        remainBoardCheck(boardRepository.findAllByMissionId(missionId));
        missionRepository.deleteById(missionId);
    }

    @Transactional(readOnly = true)
    public MissionInfoDto getMissionInfo(Long missionId) {
        Mission findMission = missionRepository.findById(missionId).orElseThrow(NoDataFoundException::new);
        return MissionInfoDto.of(findMission);
    }

    @Transactional(readOnly = true)
    public List<MissionInfoDto> getMissionInfoList(Long programId) {
        List<Mission> findResult = missionRepository.findAllByProgramId(programId);
        return findResult.stream().map(MissionInfoDto::of).collect(Collectors.toList());
    }

    private void remainBoardCheck(List<Board> boards) {
        if(!boards.isEmpty())
            throw new IllegalStateException("이미 작성된 게시글이 속해 있습니다.");
    }

}
