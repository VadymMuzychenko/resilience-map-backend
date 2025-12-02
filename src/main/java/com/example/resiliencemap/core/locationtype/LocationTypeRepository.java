package com.example.resiliencemap.core.locationtype;

import com.example.resiliencemap.core.locationtype.model.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationTypeRepository extends JpaRepository<LocationType, Long> {
    Optional<LocationType> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsBySmsCode(String smsCode);
}
