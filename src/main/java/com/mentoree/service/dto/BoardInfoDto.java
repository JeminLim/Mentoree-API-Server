package com.mentoree.service.dto;

import com.mentoree.domain.entity.Board;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardInfoDto {

    private Long id;
    private Long missionId;
    private String missionTitle;
    private Long writerId;
    private String writerNickname;
    private String title;
    private String content;
    private boolean temporal;

    public static BoardInfoDto of(Board board) {
        return BoardInfoDto.builder()
                .id(board.getId())
                .missionId(board.getMission().getId())
                .missionTitle(board.getMission().getWriting().getTitle())
                .writerId(board.getWriter().getId())
                .writerNickname(board.getWriter().getNickname())
                .title(board.getWriting().getTitle())
                .content(board.getWriting().getContent())
                .temporal(board.getTemporal())
                .build();
    }

}
