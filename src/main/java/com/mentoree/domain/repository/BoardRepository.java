package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.mission.id = :missionId")
    List<Board> findAllByMissionId(@Param("missionId") Long missionId);

}
