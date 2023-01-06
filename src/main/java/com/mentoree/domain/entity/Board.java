package com.mentoree.domain.entity;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Embedded
    private Writing writing;

    @OneToMany(mappedBy = "board", cascade = ALL, orphanRemoval = true)
    private List<AttachImage> imageFiles = new ArrayList<>();

    private Boolean temporal;

    @Builder
    public Board(Mission mission, Writing writing, Member writer, Boolean temporal) {
        Assert.notNull(mission, "mission must not be null");
        Assert.notNull(writer, "writer must not be null");

        this.mission = mission;
        this.writer = writer;
        this.writing = writing;
        this.temporal = temporal;
    }

    public void update(Writing updateWriting) {
        this.writing.updateWriting(updateWriting);
    }
    public void save() {
        this.temporal = false;
    }
    public void addImage(AttachImage image) {
        this.imageFiles.add(image);
    }

    public void cleanUpImage(List<AttachImage> confirmImage) {
        this.imageFiles.retainAll(confirmImage);
    }
}
