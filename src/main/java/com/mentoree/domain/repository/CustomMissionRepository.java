package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Mission;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomMissionRepository {
    List<Mission> findAllByProgramId(@Param("programId") Long programId, @Param("expiration") Boolean expiration);
}
