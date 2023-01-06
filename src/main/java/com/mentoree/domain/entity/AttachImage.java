package com.mentoree.domain.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Table(name = "attach_image")
public class AttachImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Embedded
    private ImageFile image;

    @Builder
    public AttachImage(ImageFile image) {
        this.image = image;
    }

    public void setBoard(Board board) {
        this.board = board;
        board.addImage(this);
    }


}
