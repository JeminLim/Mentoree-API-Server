package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Program;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomProgramRepository {
    Slice<Program> getProgramList(Long minId, String first, List<String> second, Pageable page);
    Slice<Program> getRecentProgramList(Long maxId, String first, List<String> second);

}
