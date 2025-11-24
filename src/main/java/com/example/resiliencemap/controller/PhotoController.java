package com.example.resiliencemap.controller;

import com.example.resiliencemap.core.photo.model.SaveDraftPhotoResponse;
import com.example.resiliencemap.core.photo.service.DraftPhotoService;
import com.example.resiliencemap.core.photo.service.PhotoService;
import com.example.resiliencemap.core.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final DraftPhotoService draftPhotoService;
    private final PhotoService photoService;

    @PostMapping
    public SaveDraftPhotoResponse saveDraftPhoto(@RequestParam("image") MultipartFile file,
                                                 @AuthenticationPrincipal User user) {
        return draftPhotoService.saveDraftPhoto(file, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/draft/{draftPhotoId}")
    public ResponseEntity<?> getImageByName(@PathVariable("draftPhotoId") Long draftPhotoId) {
        byte[] image = draftPhotoService.getImage(draftPhotoId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<?> getImageDataByUUID(@PathVariable("photoId") Long photoId) {
        byte[] image = photoService.getImageDataById(photoId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @DeleteMapping("/{photoId}")
    public void deletePhoto(@PathVariable("photoId") Long photoId,
                            @AuthenticationPrincipal User user) {
        photoService.deletePhoto(photoId, user);
    }
}
