package com.mentoree.domain.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String content;
    private Boolean removal;

    @Builder
    public Reply(Board board, Member writer, String content) {
        this.board = board;
        this.writer = writer;
        this.content = content;
        this.removal = false;
    }

    //==변경로직==//
    public void updateContent(String content) { this.content = content;}
    public boolean isModified() { return this.getCreatedDate() != this.getModifiedDate();}
    public void remove() {this.removal = true;}
}
