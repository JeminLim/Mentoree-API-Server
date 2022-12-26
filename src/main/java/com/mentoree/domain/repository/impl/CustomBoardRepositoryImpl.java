package com.mentoree.domain.repository.impl;

import com.mentoree.domain.entity.Board;
import com.mentoree.domain.repository.CustomBoardRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.Optional;

import static com.mentoree.domain.entity.QBoard.*;

public class CustomBoardRepositoryImpl implements CustomBoardRepository {
    private final JPAQueryFactory queryFactory;

    public CustomBoardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Board> findTemporalBoard(Long memberId, Long missionId) {
        Board findBoard = queryFactory.selectFrom(board)
                .where(board.writer.id.eq(memberId),
                        board.mission.id.eq(missionId),
                        board.temporal.eq(true))
                .fetchOne();
        return Optional.ofNullable(findBoard);
    }
}
