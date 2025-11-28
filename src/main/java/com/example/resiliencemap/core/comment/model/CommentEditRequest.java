package com.example.resiliencemap.core.comment.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentEditRequest {
    @NotBlank
    private String text;
    @Min(1)
    @Max(5)
    private Short rating;
}
