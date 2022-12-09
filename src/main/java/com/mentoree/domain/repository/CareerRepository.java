package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Career;
import com.mentoree.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Long> {
    void deleteAllByMember(Member member);
}
