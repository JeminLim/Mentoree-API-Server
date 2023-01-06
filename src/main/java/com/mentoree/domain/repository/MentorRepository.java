package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT m FROM Mentor m WHERE m.program.id = :programId and m.member.id = :memberId")
    Optional<Mentor> findByProgramIdAndMemberId(@Param("programId") Long programId, @Param("memberId") Long memberId);

    @Query("SELECT m FROM Mentor m WHERE m.member.id = :memberId")
    List<Mentor> findAllByMemberId(@Param("memberId") Long memberId);

}
