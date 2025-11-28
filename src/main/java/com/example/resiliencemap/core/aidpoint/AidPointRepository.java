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

    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.location_types lt on lt.id = p.location_type_id
            WHERE p.show_point = TRUE
                AND ST_DWithin(
                p.location,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                :radius
                )
                AND lt.id = :locationTypeId
                    AND (p.name LIKE CONCAT('%', :searchQuery, '%')
                        OR p.description LIKE CONCAT('%', :searchQuery, '%')
                        OR p.address LIKE  CONCAT('%', :searchQuery, '%'))
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    @Param("locationTypeId") long locationTypeId,
                                                    @Param("searchQuery") String searchQuery);

    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.location_types lt on lt.id = p.location_type_id
            WHERE p.show_point = TRUE
                AND ST_DWithin(
                p.location,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                :radius
                )
                AND lt.id = :locationTypeId
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    @Param("locationTypeId") long locationTypeId);

    @Query(value = """
            SELECT p.* FROM aid_points p
            WHERE p.show_point = TRUE
                AND ST_DWithin(
                p.location,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                :radius
                )
                AND (p.name LIKE CONCAT('%', :searchQuery, '%')
                    OR p.description LIKE CONCAT('%', :searchQuery, '%')
                    OR p.address LIKE  CONCAT('%', :searchQuery, '%'))
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    @Param("searchQuery") String searchQuery);

    @Query(value = """
            SELECT p.* FROM aid_points p
            WHERE ST_DWithin(
                    p.location,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                    :radius
            )
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForAdmin(@Param("lon") double lon,
                                                     @Param("lat") double lat,
                                                     @Param("radius") double radiusMeters);

    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.location_types lt on lt.id = p.location_type_id
            WHERE ST_DWithin(
                p.location,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                :radius
                )
                AND lt.id = :locationTypeId
                    AND (p.name LIKE CONCAT('%', :searchQuery, '%')
                        OR p.description LIKE CONCAT('%', :searchQuery, '%')
                        OR p.address LIKE  CONCAT('%', :searchQuery, '%'))
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForAdmin(@Param("lon") double lon,
                                                     @Param("lat") double lat,
                                                     @Param("radius") double radiusMeters,
                                                     @Param("locationTypeId") long locationTypeId,
                                                     @Param("searchQuery") String searchQuery);

    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.location_types lt on lt.id = p.location_type_id
            WHERE ST_DWithin(
                p.location,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                :radius
                )
                AND lt.id = :locationTypeId
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForAdmin(@Param("lon") double lon,
                                                     @Param("lat") double lat,
                                                     @Param("radius") double radiusMeters,
                                                     @Param("locationTypeId") long locationTypeId);

    @Query(value = """
            SELECT p.* FROM aid_points p
            WHERE ST_DWithin(
                p.location,
                ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                :radius
                )
                AND (p.name LIKE CONCAT('%', :searchQuery, '%')
                    OR p.description LIKE CONCAT('%', :searchQuery, '%')
                    OR p.address LIKE  CONCAT('%', :searchQuery, '%'))
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForAdmin(@Param("lon") double lon,
                                                     @Param("lat") double lat,
                                                     @Param("radius") double radiusMeters,
                                                     @Param("searchQuery") String searchQuery);
}
