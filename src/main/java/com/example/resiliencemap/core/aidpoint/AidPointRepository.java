package com.example.resiliencemap.core.aidpoint;

import com.example.resiliencemap.core.aidpoint.model.AidPoint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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
            WHERE p.show_point = TRUE
                AND ST_DWithin(
                    p.location,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                    :radius
            )
            """, nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    Pageable pageable);

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

    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.location_types lt on lt.id = p.location_type_id
            INNER JOIN public.aid_point_services aps ON aps.aid_point_id = p.id
            INNER JOIN public.service_types st ON st.id = aps.service_type_id
            WHERE p.show_point = TRUE
              AND lt.sms_code = :locationSmsCode
              AND st.sms_code = ANY(string_to_array(:servicesString, ','))
              AND ST_DWithin(
                    p.location,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                    :radius
                  )""", nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    @Param("locationSmsCode") String locationSmsCode,
                                                    @Param("servicesString") String servicesString,
                                                    Pageable pageable);


    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.aid_point_services aps ON aps.aid_point_id = p.id
            INNER JOIN public.service_types st ON st.id = aps.service_type_id
            WHERE p.show_point = TRUE
              AND st.sms_code = ANY(:servicesSmsCodes)
              AND ST_DWithin(
                    p.location,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                    :radius
                  )""", nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    @Param("servicesSmsCodes") Collection<String> servicesSmsCodes,
                                                    Pageable pageable);


    @Query(value = """
            SELECT p.* FROM aid_points p
            INNER JOIN public.location_types lt on lt.id = p.location_type_id
            WHERE p.show_point = TRUE
              AND lt.sms_code = :locationSmsCode
              AND ST_DWithin(
                    p.location,
                    ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography,
                    :radius
                  )""", nativeQuery = true)
    List<AidPoint> findAidPointsWithinRadiusForUser(@Param("lon") double lon,
                                                    @Param("lat") double lat,
                                                    @Param("radius") double radiusMeters,
                                                    @Param("locationSmsCode") String locationSmsCode,
                                                    Pageable pageable);
}
