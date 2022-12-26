package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Board;

import java.util.Optional;

public interface CustomBoardRepository {

    Optional<Board> findTemporalBoard(Long memberId, Long missionId);

}
