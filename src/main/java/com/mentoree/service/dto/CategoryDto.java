package com.mentoree.service.dto;

import com.mentoree.domain.entity.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private String categoryName;
    private String parentCategory;

    public static CategoryDto of(Category category) {
        CategoryDtoBuilder builder = CategoryDto.builder();
        builder.categoryName(category.getCategoryName());
        if(category.getParent() != null)
            builder.parentCategory(category.getParent().getCategoryName());

        return builder.build();
    }

}
