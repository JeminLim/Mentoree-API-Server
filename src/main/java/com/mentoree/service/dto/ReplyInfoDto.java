package com.mentoree.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mentoree.domain.entity.Reply;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyInfoDto {

    private Long id;
    private Long boardId;
    private Long memberId;
    private String memberNickname;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedDate;
    private Boolean isModified;
    private Boolean removal;

    public static ReplyInfoDto of(Reply reply) {
        return ReplyInfoDto.builder()
                .id(reply.getId())
                .boardId(reply.getBoard().getId())
                .memberId(reply.getWriter().getId())
                .memberNickname(reply.getWriter().getNickname())
                .content(reply.getContent())
                .modifiedDate(reply.getModifiedDate())
                .isModified(reply.isModified())
                .removal(reply.getRemoval())
                .build();
    }
}
