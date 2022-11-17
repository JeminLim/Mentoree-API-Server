package com.mentoree.domain.entity;

import com.mysema.commons.lang.Assert;
import lombok.*;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Categories")
@EqualsAndHashCode
public class Category extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String categoryName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parent;


    @OneToMany(fetch = LAZY, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children;

    @Builder
    public Category(String categoryName) {
        Assert.notNull(categoryName, "category name must not be null");
        this.categoryName = categoryName;
    }
}
