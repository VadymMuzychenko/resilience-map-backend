package com.example.resiliencemap.core.verification.model;

import com.example.resiliencemap.core.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "verification_codes")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_codes_id_gen")
    @SequenceGenerator(name = "verification_codes_id_gen", sequenceName = "verification_codes_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 10)
    @NotNull
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Size(max = 255)
    @NotNull
    @Column(name = "destination", nullable = false)
    private String destination;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @ColumnDefault("false")
    @Column(name = "used", nullable = false)
    private Boolean used;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "contact_method not null")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private VerificationContactMethod type;

}