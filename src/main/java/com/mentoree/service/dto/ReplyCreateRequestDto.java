package com.mentoree.service.dto;

import com.mentoree.domain.entity.Board;
import com.mentoree.domain.entity.Member;
import com.mentoree.domain.entity.Reply;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreateRequestDto {

    private Long boardId;
    private String content;

    public Reply toEntity(Board board, Member writer) {
        return Reply.builder()
                .board(board)
                .writer(writer)
                .content(content)
                .build();
    }

}
