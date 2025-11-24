package com.example.resiliencemap.core.photo.model;

import com.example.resiliencemap.core.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "draft_photo")
public class DraftPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "draft_photo_id_gen")
    @SequenceGenerator(name = "draft_photo_id_gen", sequenceName = "draft_photos_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "image_data", nullable = false)
    private byte[] imageData;

    @ColumnDefault("now()")
    @Column(name = "uploaded_at", nullable = false)
    private OffsetDateTime uploadedAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

}