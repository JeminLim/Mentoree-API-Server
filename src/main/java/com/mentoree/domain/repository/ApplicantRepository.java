package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Applicant;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    @Query(value = "SELECT a FROM Applicant a WHERE a.program.id = :programId AND a.approval = false")
    List<Applicant> findAllByProgramId(@Param("programId") Long programId);

    Optional<Applicant> findByMemberAndProgram(Member member, Program program);
}
