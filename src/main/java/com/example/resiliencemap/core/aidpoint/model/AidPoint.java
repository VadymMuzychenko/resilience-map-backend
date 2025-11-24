package com.example.resiliencemap.core.aidpoint.model;

import com.example.resiliencemap.core.locationtype.model.LocationType;
import com.example.resiliencemap.core.servicetype.model.ServiceType;
import com.example.resiliencemap.core.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "aid_points")
public class AidPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aid_points_id_gen")
    @SequenceGenerator(name = "aid_points_id_gen", sequenceName = "aid_points_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;


    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    private Point location;

    @ManyToMany
    @JoinTable(name = "aid_point_services",
            joinColumns = @JoinColumn(name = "aid_point_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id"))
    private Set<ServiceType> serviceTypes = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ColumnDefault("false")
    @Column(name = "show_point")
    private Boolean showPoint;

    @Size(max = 255)
    @Column(name = "address")
    private String address;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "location_type_id", nullable = false)
    private LocationType locationType;

    @NotNull
    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AidPointStatus status;

}