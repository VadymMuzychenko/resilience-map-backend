package com.example.resiliencemap.core.photo.repository;

import com.example.resiliencemap.core.photo.model.Photo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAidPoint_Id(Long aidPointId, Sort sort);
    List<Photo> getPhotosByComment_Id(Long commentId, Sort sort);
}