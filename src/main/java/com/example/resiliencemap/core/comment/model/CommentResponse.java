package com.example.resiliencemap.core.comment.model;

import com.example.resiliencemap.core.user.model.UserLightweightResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class CommentResponse {
    private Long id;
//    private Long aidPointId; TODO
    private UserLightweightResponse user;
    private String text;
    private Short rating;
    private OffsetDateTime createdAt;
    private List<Long> photos;
}
