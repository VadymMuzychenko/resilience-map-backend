package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.comment.CommentService;
import com.example.resiliencemap.core.comment.model.CommentCreateRequest;
import com.example.resiliencemap.core.comment.model.CommentEditRequest;
import com.example.resiliencemap.core.comment.model.CommentResponse;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.model.PagingList;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/aid-point/{aidPointId}/comment")
    public CommentResponse addComment(@PathVariable("aidPointId") Long aidPointId,
                                      @Valid @RequestBody CommentCreateRequest request,
                                      @AuthenticationPrincipal User user) {
        return commentService.addComment(aidPointId, request, user);
    }

    @GetMapping("/aid-point/{aidPointId}/comments")
    public PagingList<CommentResponse> getComments(@PathVariable("aidPointId") Long aidPointId,
                                                   @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   @AuthenticationPrincipal User user) {
        return commentService.getAidPointComments(aidPointId, pageNumber, pageSize, user);
    }

    @PostMapping("/comment/{commentId}/edit")
    public CommentResponse editComment(@PathVariable("commentId") Long commentId,
                                       @Valid @RequestBody CommentEditRequest request,
                                       @AuthenticationPrincipal User user) {
        return commentService.editComment(commentId, request, user);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId,
                              @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
    }

}
