package com.mentoree.domain.repository;

import com.mentoree.domain.entity.AttachImage;
import com.mentoree.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachImageRepository extends JpaRepository<AttachImage, Long> {

    List<AttachImage> findAllByBoard(Board board);

}
