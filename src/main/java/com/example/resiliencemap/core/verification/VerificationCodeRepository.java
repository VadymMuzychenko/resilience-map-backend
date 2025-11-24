package com.example.resiliencemap.core.verification;

import com.example.resiliencemap.core.verification.model.VerificationCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    @Query("""
            SELECT vc FROM VerificationCode vc
                WHERE vc.destination = :destination
                AND vc.code = :code
                AND vc.used = FALSE
                AND vc.expiresAt > CURRENT_TIMESTAMP
                ORDER BY vc.createdAt DESC
            """)
    List<VerificationCode> findLastUnusedCode(@Param("destination") String destination, @Param("code") String code, Pageable pageable);

    @Query("""
            SELECT vc FROM VerificationCode vc
                WHERE vc.destination = :destination
                AND vc.used = TRUE
                ORDER BY vc.createdAt DESC
            """)
    List<VerificationCode> findLastUsedCode(@Param("destination") String destination, Pageable pageable);
}
