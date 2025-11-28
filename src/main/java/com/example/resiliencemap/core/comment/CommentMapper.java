package com.example.resiliencemap.core.comment;

import com.example.resiliencemap.core.comment.model.Comment;
import com.example.resiliencemap.core.comment.model.CommentResponse;
import com.example.resiliencemap.core.user.UserService;
import com.example.resiliencemap.core.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final UserService userService;

    public CommentResponse toCommentResponse(Comment comment, List<Long> photos, User actor) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setRating(comment.getRating());
        userService.toUserResponse(comment.getUser(), actor);
        response.setCreatedAt(comment.getCreatedAt());
        response.setPhotos(photos);
        return response;
    }
}
