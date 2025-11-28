package com.example.resiliencemap.core.photo.service;

import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import com.example.resiliencemap.core.comment.model.Comment;
import com.example.resiliencemap.core.photo.model.Photo;
import com.example.resiliencemap.core.photo.repository.PhotoRepository;
import com.example.resiliencemap.core.user.model.User;
import com.example.resiliencemap.functional.exception.ForbiddenException;
import com.example.resiliencemap.functional.exception.NotFoundException;
import com.example.resiliencemap.functional.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final TransactionalPhotoMover transactionalPhotoMover;
    private final PhotoRepository photoRepository;

    public List<Long> moveDraftPhotosToAidPointPhotos(List<Long> ids, AidPoint aidPoint) {
        ListIterator<Long> iterator = ids.listIterator();
        ArrayList<Long> newIds = new ArrayList<>();
        boolean setPrimary = false;
        while (iterator.hasNext()) {
            Long id = iterator.next();
            if (!setPrimary) {
                newIds.add(transactionalPhotoMover.movePhotoFromDraftToAidPointPhoto(id, true, aidPoint));
                setPrimary = true;
            } else {
                newIds.add(transactionalPhotoMover.movePhotoFromDraftToAidPointPhoto(id, false, aidPoint));
            }
        }
        return newIds;
    }

    public List<Long> moveDraftPhotosToCommentPhotos(List<Long> ids, Comment comment) {
        ListIterator<Long> iterator = ids.listIterator();
        ArrayList<Long> newIds = new ArrayList<>();
        boolean setPrimary = false;
        while (iterator.hasNext()) {
            Long id = iterator.next();
            if (!setPrimary) {
                newIds.add(transactionalPhotoMover.movePhotoFromDraftToCommentPhoto(id, true, comment));
                setPrimary = true;
            } else {
                newIds.add(transactionalPhotoMover.movePhotoFromDraftToCommentPhoto(id, false, comment));
            }
        }
        return newIds;
    }

    public List<Long> getPhotoIDsByAidPoint(Long aidPointId) {
        List<Photo> photos = photoRepository.findByAidPoint_Id(aidPointId,
                Sort.by(Sort.Order.asc("isPrimary")));
        return photos.stream().map(Photo::getId).toList();
    }

    public byte[] getImageDataById(Long id) {
        Photo photo = getPhoto(id);
        return ImageUtil.decompressImage(photo.getImageData());
    }

    public Photo getPhoto(Long id) {
        Optional<Photo> dbImage = photoRepository.findById(id);
        if (dbImage.isPresent()) {
            return dbImage.get();
        } else {
            throw new NotFoundException("Photo with this ID was not found: " + id);
        }
    }

    public List<Long> getPhotoUuidsByComment(Long commentId) {
        List<Photo> photos = photoRepository.getPhotosByComment_Id(commentId,
                Sort.by(Sort.Order.asc("isPrimary")));
        return photos.stream().map(Photo::getId).toList();
    }

    public void deletePhoto(Long id, User author) {
        Optional<Photo> optional = photoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Photo with id " + id + " not found");
        }
        Photo photo = optional.get();
        if (!photo.getAidPoint().getCreatedBy().equals(author)) {
            throw new ForbiddenException("The user is not the author of the photo");
        }
        photoRepository.deleteById(id);
    }
}
