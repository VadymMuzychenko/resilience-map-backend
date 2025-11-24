package com.example.resiliencemap.core.photo.service;

import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import com.example.resiliencemap.core.comment.model.Comment;
import com.example.resiliencemap.core.photo.model.DraftPhoto;
import com.example.resiliencemap.core.photo.model.Photo;
import com.example.resiliencemap.core.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionalPhotoMover {

    private final PhotoRepository photoRepository;
    private final DraftPhotoService draftPhotoService;

    @Transactional
    public Long movePhotoFromDraftToWCPhoto(Long draftUUID, boolean isPrimary, AidPoint aidPoint) {
        DraftPhoto draftPhoto = draftPhotoService.getPhoto(draftUUID);
        Long savedUuid = savePhoto(draftPhoto, isPrimary, aidPoint);
        draftPhotoService.removeDraftPhoto(draftUUID);
        return savedUuid;
    }

    private Long savePhoto(DraftPhoto draftPhoto, boolean isPrimary, AidPoint aidPoint) {
        Photo photoToSave = new Photo();
        photoToSave.setImageData(draftPhoto.getImageData());
        photoToSave.setIsPrimary(isPrimary);
        photoToSave.setAidPoint(aidPoint);
        photoToSave.setUploadedAt(draftPhoto.getUploadedAt());
        Photo saved = photoRepository.save(photoToSave);
        return saved.getId();
    }

    @Transactional
    public Long movePhotoFromDraftToCommentPhoto(Long draftUUID, boolean isPrimary, Comment comment) {
        DraftPhoto draftPhoto = draftPhotoService.getPhoto(draftUUID);
        Long savedUuid = savePhoto(draftPhoto, isPrimary, comment);
        draftPhotoService.removeDraftPhoto(draftUUID);
        return savedUuid;
    }

    private Long savePhoto(DraftPhoto draftPhoto, boolean isPrimary, Comment comment) {
        Photo photoToSave = new Photo();
        photoToSave.setImageData(draftPhoto.getImageData());
        photoToSave.setIsPrimary(isPrimary);
        photoToSave.setComment(comment);
        photoToSave.setUploadedAt(draftPhoto.getUploadedAt());
        Photo saved = photoRepository.save(photoToSave);
        return saved.getId();
    }
}
