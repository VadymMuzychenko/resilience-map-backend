package com.example.resiliencemap.core.comment;

import com.example.resiliencemap.core.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByAidPoint_Id(Long aidPointId, Pageable pageable);
}
