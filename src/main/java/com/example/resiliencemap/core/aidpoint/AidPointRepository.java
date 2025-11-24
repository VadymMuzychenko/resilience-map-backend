package com.example.resiliencemap.core.aidpoint;

import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AidPointRepository extends JpaRepository<AidPoint, Long> {

    @Query(value = """
            SELECT p.* FROM aid_points p
            WHERE p.show_point = TRUE 
                AND ST_DWithin(
                    p.location,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                    :radius
            )
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters);
}
