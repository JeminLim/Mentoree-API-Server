package com.mentoree.domain.repository.impl;

import com.mentoree.domain.entity.*;
import com.mentoree.domain.repository.CustomProgramRepository;
import com.mentoree.domain.repository.util.RepositoryHelper;
import com.mentoree.service.dto.MentorDto;
import com.mentoree.service.dto.ProgramInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mentoree.domain.entity.QCategory.*;
import static com.mentoree.domain.entity.QMember.*;
import static com.mentoree.domain.entity.QMentor.*;
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
                        program.id.gt(maxId),
                        matchFirstCategory(first),
                        matchSecondCategory(second))
                .orderBy(program.id.desc())
                .limit(page.getPageSize() + 1)
                .offset(page.getOffset())
                .fetch();
        return RepositoryHelper.toSlice(queryResult, page);
    }

    @Override
    public Slice<Program> getPrograms(Long minId, Long maxId, String first, List<String> second) {
        Pageable page = PageRequest.of(0, 8);
        List<Program> queryResult = queryFactory.selectFrom(program)
                .where(openProgram(),
                        program.id.gt(maxId),
                        program.id.lt(minId),
                        matchFirstCategory(first),
                        matchSecondCategory(second))
                .orderBy(program.id.desc())
                .limit(page.getPageSize() + 1)
                .offset(page.getOffset())
                .fetch();
        return RepositoryHelper.toSlice(queryResult, page);
    }

    @Override
    public Slice<ProgramInfoDto> getRecentProgramDtoList(Long maxId, String first, List<String> second) {
        Pageable page = PageRequest.of(0, 8);
        List<ProgramInfoDto> programInfoList = queryFactory.select(Projections.fields(ProgramInfoDto.class,
                        program.id,
                        program.programName,
                        program.description,
                        program.maxMember,
                        program.price,
                        program.dueDate,
                        category.categoryName.as("category")))
                .from(program)
                .leftJoin(program.category, category)
                .where(openProgram(),
                        program.id.gt(maxId),
                        matchFirstCategory(first),
                        matchSecondCategory(second))
                .orderBy(program.id.desc())
                .limit(page.getPageSize() + 1)
                .offset(page.getOffset())
                .fetch();

        List<Long> programIds = programInfoList.stream().map(ProgramInfoDto::getId).collect(Collectors.toList());

        Map<Long, List<MentorDto>> mentorMap = queryFactory.select(
                        Projections.fields(MentorDto.class,
                                mentor.member.id.as("memberId"),
                                mentor.program.id.as("programId"),
                                mentor.member.username,
                                mentor.program.programName,
                                mentor.host))
                .from(mentor)
                .leftJoin(mentor.member, member)
                .leftJoin(mentor.program, program)
                .where(mentor.program.id.in(programIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(MentorDto::getProgramId));

        for (ProgramInfoDto programInfoDto : programInfoList) {
            Long programId = programInfoDto.getId();
            List<MentorDto> mentors = mentorMap.get(programId);
            programInfoDto.setMentorList(mentors);
        }
        return RepositoryHelper.toSlice(programInfoList, page);
    }

    @Override
    public Slice<ProgramInfoDto> getProgramDtoList(Long minId, String first, List<String> second, Pageable page) {
        List<ProgramInfoDto> programInfoList = queryFactory.select(Projections.fields(ProgramInfoDto.class,
                        program.id,
                        program.programName,
                        program.description,
                        program.maxMember,
                        program.price,
                        program.dueDate,
                        category.categoryName.as("category")))
                .from(program)
                .leftJoin(program.category, category)
                .where(openProgram(),
                        ltProgramId(minId),
                        matchFirstCategory(first),
                        matchSecondCategory(second))
                .orderBy(program.id.desc())
                .limit(page.getPageSize() + 1)
                .offset(page.getOffset())
                .fetch();

        List<Long> programIds = programInfoList.stream().map(ProgramInfoDto::getId).collect(Collectors.toList());
        Map<Long, List<MentorDto>> mentorMap = queryFactory.select(
                        Projections.fields(MentorDto.class,
                                mentor.member.id.as("memberId"),
                                mentor.program.id.as("programId"),
                                mentor.member.username,
                                mentor.program.programName,
                                mentor.host))
                .from(mentor)
                .leftJoin(mentor.member, member)
                .leftJoin(mentor.program, program)
                .where(mentor.program.id.in(programIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(MentorDto::getProgramId));

        for (ProgramInfoDto programInfoDto : programInfoList) {
            Long programId = programInfoDto.getId();
            List<MentorDto> mentors = mentorMap.get(programId);
            programInfoDto.setMentorList(mentors);
        }
        return RepositoryHelper.toSlice(programInfoList, page);
    }

    private BooleanExpression ltProgramId(Long minId) {
        return minId == 0 ? null : program.id.lt(minId);
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
