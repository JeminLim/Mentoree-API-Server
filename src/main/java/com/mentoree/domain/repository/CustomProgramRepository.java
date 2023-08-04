package com.mentoree.domain.repository;

import com.mentoree.domain.entity.Program;
import com.mentoree.service.dto.ProgramInfoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomProgramRepository {
    Slice<Program> getProgramList(Long minId, String first, List<String> second, Pageable page);
    Slice<Program> getRecentProgramList(Long maxId, String first, List<String> second);
    Slice<Program> getPrograms(Long minId, Long maxId, String first, List<String> second);
    Slice<ProgramInfoDto> getRecentProgramDtoList(Long maxId, String first, List<String> second);
    Slice<ProgramInfoDto> getProgramDtoList(Long minId, String first, List<String> second, Pageable page);

}
