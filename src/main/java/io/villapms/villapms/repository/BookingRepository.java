// BookingRepository.java
package io.villapms.villapms.repository;

import io.villapms.villapms.model.Booking.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPropertyIdOrderByCheckinDateAsc(Long propertyId);

    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND NOT (b.checkoutDate < :start OR b.checkinDate > :end)")
    List<Booking> findOverlapping(
            @Param("propertyId") Long propertyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<Booking> findByUserId(Long userId);

    List<Booking> findByPropertyIdAndUserId(Long propertyId, Long userId);
}