package com.example.resiliencemap.core.servicetype;

import com.example.resiliencemap.core.servicetype.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    boolean existsByCode(String code);

    boolean existsBySmsCode(String smsCode);
}
