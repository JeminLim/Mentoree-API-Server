package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long>, CustomProgramRepository {


}
