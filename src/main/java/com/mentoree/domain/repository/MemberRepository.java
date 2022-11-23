package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    public boolean existsByEmail(String email);
    public boolean existsByNickname(String nickname);

}
