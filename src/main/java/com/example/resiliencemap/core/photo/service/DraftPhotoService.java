package com.example.resiliencemap.core.photo.service;

import com.example.resiliencemap.core.photo.model.DraftPhoto;
import com.example.resiliencemap.core.photo.model.SaveDraftPhotoResponse;
import com.example.resiliencemap.core.photo.repository.DraftPhotoRepository;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.utils.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DraftPhotoService {

    private final DraftPhotoRepository draftPhotoRepository;

    public SaveDraftPhotoResponse saveDraftPhoto(MultipartFile file, User user) {
        try {
            DraftPhoto photoToSave = new DraftPhoto();
            photoToSave.setImageData(ImageUtil.compressImage(file.getBytes()));
            photoToSave.setExpiresAt(OffsetDateTime.now().plusMonths(1));
            photoToSave.setUploadedAt(OffsetDateTime.now());
            photoToSave.setUploadedBy(user);
            DraftPhoto saved = draftPhotoRepository.save(photoToSave);
            return new SaveDraftPhotoResponse(saved.getId());
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Transactional
    public byte[] getImage(Long id) {
        Optional<DraftPhoto> dbImage = draftPhotoRepository.findById(id);
        byte[] image = ImageUtil.decompressImage(dbImage.get().getImageData());
        return image;
    }

    public DraftPhoto getPhoto(Long id) {
        Optional<DraftPhoto> dbImage = draftPhotoRepository.findById(id);
        return dbImage.get();
    }

    public void removeDraftPhoto(Long draftPhotoId) {
        draftPhotoRepository.deleteById(draftPhotoId);
    }

}

