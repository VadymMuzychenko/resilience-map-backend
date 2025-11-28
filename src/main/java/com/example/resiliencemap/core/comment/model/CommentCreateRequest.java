package com.example.resiliencemap.core.comment.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentCreateRequest {
    @NotBlank
    private String text;
    @Min(1)
    @Max(5)
    private Short rating;
    private List<Long> draftPhotoIds;
}
