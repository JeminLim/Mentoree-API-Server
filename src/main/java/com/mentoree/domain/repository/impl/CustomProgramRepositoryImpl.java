package com.mentoree.domain.repository.impl;

import com.mentoree.domain.entity.Program;
import com.mentoree.domain.entity.ProgramState;
import com.mentoree.domain.entity.QCategory;
import com.mentoree.domain.entity.QProgram;
import com.mentoree.domain.repository.CustomProgramRepository;
import com.mentoree.domain.repository.util.RepositoryHelper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mentoree.domain.entity.QProgram.*;

public class CustomProgramRepositoryImpl implements CustomProgramRepository {
    private final JPAQueryFactory queryFactory;

    public CustomProgramRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Slice<Program> getProgramList(Long minId, String first, List<String> second, Pageable page) {
            List<Program> queryResult = queryFactory.selectFrom(program)
                    .where( openProgram(),
                            ltProgramId(minId),
                            matchFirstCategory(first),
                            matchSecondCategory(second))
                    .orderBy(program.id.desc())
                    .limit(page.getPageSize() + 1)
                    .offset(page.getOffset())
                    .fetch();
        return RepositoryHelper.toSlice(queryResult, page);
    }

    @Override
    public Slice<Program> getRecentProgramList(Long maxId, String first, List<String> second) {
        Pageable page = PageRequest.of(0, 8);
        List<Program> queryResult = queryFactory.selectFrom(program)
                .where(openProgram(),
                        gtProgramId(maxId),
                        matchFirstCategory(first),
                        matchSecondCategory(second))
                .orderBy(program.id.asc())
                .limit(page.getPageSize() + 1)
                .offset(page.getOffset())
                .fetch();
        return RepositoryHelper.toSlice(queryResult, page);
    }

    private BooleanExpression ltProgramId(Long minId) {
        return minId == null ? null : program.id.lt(minId);
    }

    private BooleanExpression gtProgramId(Long maxId) {
        return maxId == null ? null : program.id.gt(maxId);
    }

    private BooleanExpression matchFirstCategory(String category) {
        return category == null ? null : program.category.parent.categoryName.eq(category);
    }

    private BooleanExpression matchSecondCategory(List<String> category) {
        return category == null ? null : program.category.categoryName.in(category);
    }

    private BooleanExpression openProgram() {
        return program.state.eq(ProgramState.OPEN);
    }

}
