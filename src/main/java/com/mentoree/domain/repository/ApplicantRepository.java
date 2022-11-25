package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    @Query(value = "SELECT a FROM Applicant a WHERE a.program.id = :programId")
    List<Applicant> findAllByProgramId(@Param("programId") Long programId);
}
