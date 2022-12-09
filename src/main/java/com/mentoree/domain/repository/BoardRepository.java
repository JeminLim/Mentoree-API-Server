package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.mission.id = :missionId")
    List<Board> findAllByMissionId(@Param("missionId") Long missionId);

    // fetch 조인으로 한방에 조회하는 것 => 인터셉터 완료 => 테스트 완료 => 시큐리티 완료
    @Query("SELECT b FROM Board b JOIN FETCH b.mission WHERE b.id = :boardId")
    Optional<Board> fetchFindById(@Param("boardId") Long boardId);
}
