package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {

}
