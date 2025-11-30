package com.example.resiliencemap.core.comment;

import com.example.resiliencemap.core.aidpoint.AidPointService;
import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import com.example.resiliencemap.core.comment.model.Comment;
import com.example.resiliencemap.core.comment.model.CommentCreateRequest;
import com.example.resiliencemap.core.comment.model.CommentEditRequest;
import com.example.resiliencemap.core.comment.model.CommentResponse;
import com.example.resiliencemap.core.photo.service.PhotoService;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.exception.ForbiddenException;
import com.example.resiliencemap.functional.exception.NotFoundException;
import com.example.resiliencemap.functional.model.PagingList;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AidPointService aidPointService;
    private final PhotoService photoService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse addComment(Long aidPointId, CommentCreateRequest request, User user) {
        AidPoint aidPoint = aidPointService.getAidPointById(aidPointId);
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setRating(request.getRating());
        comment.setUser(user);
        comment.setAidPoint(aidPoint);
        comment.setCreatedAt(OffsetDateTime.now());
        Comment saved = commentRepository.save(comment);
        if (request.getDraftPhotoIds() != null && !request.getDraftPhotoIds().isEmpty()) {
            List<Long> movedUuids = photoService.moveDraftPhotosToCommentPhotos(request.getDraftPhotoIds(), saved);
            return commentMapper.toCommentResponse(saved, movedUuids, user);
        } else {
            return commentMapper.toCommentResponse(saved, null, user);
        }
    }

    public PagingList<CommentResponse> getAidPointComments(Long aidPointId, int pageNumber, int pageSize, User user) {
        AidPoint aidPoint = aidPointService.getAidPointById(aidPointId); // TODO

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Comment> page = commentRepository.findByAidPoint_Id(aidPointId, pageable);
        List<CommentResponse> responseList = page.stream().map(c -> {
            List<Long> photos = photoService.getPhotoIdsByComment(c.getId());
            return commentMapper.toCommentResponse(c, photos, user);
        }).toList();
        return new PagingList<>(pageNumber, page.getTotalPages(), page.getTotalElements(), responseList);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));

    }

    public CommentResponse editComment(Long commentId, CommentEditRequest request, User user) {
        Comment comment = getCommentById(commentId);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access Denied");
        }
        comment.setText(request.getText());
        comment.setRating(request.getRating());
        Comment saved = commentRepository.save(comment);
        List<Long> photos = photoService.getPhotoIdsByComment(saved.getId());
        return commentMapper.toCommentResponse(saved, photos, user);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = getCommentById(commentId);
        if (!(comment.getUser().equals(user) || User.UserRole.ADMIN.equals(comment.getUser().getRole()))) {
            throw new ForbiddenException("Access Denied");
        }
        commentRepository.delete(comment);
    }
}
