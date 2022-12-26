package com.mentoree.domain.repository.impl;

import com.mentoree.domain.entity.Mission;
import com.mentoree.domain.repository.CustomMissionRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.mentoree.domain.entity.QMission.*;
import static com.mentoree.domain.entity.QProgram.*;

public class CustomMissionRepositoryImpl implements CustomMissionRepository {

    private final JPAQueryFactory queryFactory;

    public CustomMissionRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Mission> findAllByProgramId(Long programId, Boolean expiration) {
        return queryFactory.selectFrom(mission)
                .join(mission.program, program)
                .fetchJoin()
                .where(mission.program.id.eq(programId),
                        isTerminate(expiration))
                .fetch();
    }

    private BooleanExpression isTerminate(Boolean expiration) {
        return expiration ? mission.dueDate.before(LocalDate.now()) : mission.dueDate.after(LocalDate.now());
    }
}
