package com.mentoree.config.utils.files;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FileInfo {

    private String savedFileName;
    private String name;
    private String extension;
    private String path;
    private long size;

    @Builder.Default
    private LocalDateTime createAd = LocalDateTime.now();

    public static FileInfo multipartOf(MultipartFile multipartFile, String savedFileName) {
        final String extension = MultipartUtils.getExtension(multipartFile.getOriginalFilename());
        return FileInfo.builder()
                .savedFileName(savedFileName)
                .name(multipartFile.getOriginalFilename())
                .extension(extension)
                .path(MultipartUtils.createPath(savedFileName, extension))
                .size(multipartFile.getSize())
                .build();
    }

}
