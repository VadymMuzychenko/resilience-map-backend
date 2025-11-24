package com.example.resiliencemap.core.locationtype.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "location_types")
public class LocationType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_types_id_gen")
    @SequenceGenerator(name = "location_types_id_gen", sequenceName = "location_types_sequence", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Size(max = 10)
    @NotNull
    @Column(name = "sms_code", nullable = false, length = 10)
    private String smsCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

}