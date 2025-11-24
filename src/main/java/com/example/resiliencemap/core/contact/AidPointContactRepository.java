package com.example.resiliencemap.core.contact;

import com.example.resiliencemap.core.contact.model.AidPointContact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AidPointContactRepository extends JpaRepository<AidPointContact, Long> {
    List<AidPointContact> findByAidPoint_Id(Long aidPointId);

    @Query("""
            SELECT a FROM AidPointContact a
            WHERE a.aidPoint.id = ?1 
                        AND (a.fullName LIKE %?2% OR a.phoneNumber LIKE %?2%)
            ORDER BY a.fullName, a.phoneNumber, a.id""")
    List<AidPointContact> findContactsForAdmin(Long id, String searchData);

    @Query("""
            SELECT a FROM AidPointContact a
            WHERE a.aidPoint.id = ?1
                        AND (a.hide = FALSE OR a.aidPoint.createdBy.id = ?2)
                        AND (a.fullName LIKE %?2% OR a.phoneNumber LIKE %?2%)
            ORDER BY a.fullName, a.phoneNumber, a.id""")
    List<AidPointContact> findContactsForUser(Long id, Long createdByUserId, String searchData);

    @Query("select (count(a) > 0) from AidPointContact a where a.aidPoint.createdBy.id = ?1")
    boolean existsByAidPoint_CreatedBy_Id(Long id);

    @Query("select (count(a) > 0) from AidPointContact a where a.phoneNumber = ?1")
    boolean existsByPhoneNumber(String phoneNumber);


    @Query("""
            SELECT a FROM AidPointContact a
            WHERE a.fullName LIKE %?1% OR a.phoneNumber LIKE %?1%
            ORDER BY a.fullName, a.phoneNumber, a.id""")
    List<AidPointContact> findAllContacts(String searchData, Pageable pageable);

}
