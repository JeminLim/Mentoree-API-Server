package com.mentoree.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Writing {

    private String title;
    private String content;

    public Writing(String title, String content) {
        secureConvert(content);
        this.title = title;
        this.content = content;
    }

    public void updateWriting(Writing updateWriting) {
        secureConvert(updateWriting.getContent());
        this.title = updateWriting.getTitle();
        this.content = updateWriting.getContent();
    }

    private void secureConvert(String content) {
        // Secure coding logic
    }
}
