// BookingRepository.java
package io.villapms.villapms.repository;

import io.villapms.villapms.model.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVillaIdOrderByStartDateAsc(Long villaId);

    @Query("SELECT b FROM Booking b WHERE b.villa.id = :villaId AND NOT (b.endDate < :start OR b.startDate > :end)")
    List<Booking> findOverlapping(
            @Param("villaId") Long villaId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
