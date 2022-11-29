package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT r FROM Reply r WHERE r.board.id = :boardId")
    List<Reply> findAllByBoardId(@Param("boardId") Long boardId);
}
