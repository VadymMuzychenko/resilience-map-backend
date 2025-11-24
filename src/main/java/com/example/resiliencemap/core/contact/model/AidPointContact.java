package com.example.resiliencemap.core.contact.model;

import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "aid_point_contacts")
public class AidPointContact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aid_point_contacts_id_gen")
    @SequenceGenerator(name = "aid_point_contacts_id_gen", sequenceName = "aid_point_contacts_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "aid_point_id", nullable = false)
    private AidPoint aidPoint;

    @Size(max = 255)
    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 100)
    @NotNull
    @ColumnDefault("'creator'")
    @Column(name = "role", nullable = false, length = 100)
    private String role;

    @ColumnDefault("false")
    @Column(name = "hide")
    private Boolean hide;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}