package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {

    @Query("SELECT m FROM Mentee m WHERE m.program.id = :programId AND m.member.id = :memberId")
    Optional<Mentee> findByProgramIdAndMemberId(@Param("programId") Long programId, @Param("memberId") Long memberId);

    @Query("SELECT m FROM Mentee m WHERE m.member.id = :memberId AND m.payment = true")
    List<Mentee> findAllParticipatedMentee(@Param("memberId") Long memberId);
}
