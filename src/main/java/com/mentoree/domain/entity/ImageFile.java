package com.mentoree.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageFile {

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "origin_filename")
    private String originFileName;

    @Column(name = "save_filename")
    private String saveFileName;

    @Column(name = "media_type")
    private String mediaType;

}
