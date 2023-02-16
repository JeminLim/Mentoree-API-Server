package com.mentoree.config.utils.files;

import lombok.Getter;

import java.io.File;

@Getter
public class FilePathBuilder {
    String path = "";
    public FilePathBuilder addPath(String path) {
        this.path = this.path.concat(path).concat(File.separator);
        return this;
    }

    public String build() {
        return this.path;
    }

}
