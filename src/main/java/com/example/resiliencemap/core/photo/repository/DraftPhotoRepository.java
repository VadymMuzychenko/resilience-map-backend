package com.example.resiliencemap.core.photo.repository;

import com.example.resiliencemap.core.photo.model.DraftPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftPhotoRepository extends JpaRepository<DraftPhoto, Long> {
}
