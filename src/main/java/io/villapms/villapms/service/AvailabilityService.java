package io.villapms.villapms.service;

import io.villapms.villapms.model.Booking;
import io.villapms.villapms.model.Villa;
import io.villapms.villapms.repository.BookingRepository;
import io.villapms.villapms.repository.VillaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AvailabilityService {
    private final BookingRepository bookingRepo;
    private final VillaRepository villaRepo;

    public AvailabilityService(BookingRepository bookingRepo, VillaRepository villaRepo) {
        this.bookingRepo = bookingRepo;
        this.villaRepo   = villaRepo;
    }

    // 1) Return all existing bookings for a given villa (so frontend can mark those dates as “taken”)
    public List<Booking> getAllBookings(Long villaId) {
        return bookingRepo.findByVillaIdOrderByStartDateAsc(villaId);
    }

    // 2) Check whether a requested date‐range overlaps any existing booking
    public boolean isDateRangeAvailable(Long villaId, LocalDate start, LocalDate end) {
        List<Booking> overlaps = bookingRepo.findOverlapping(villaId, start, end);
        return overlaps.isEmpty();
    }

    // 3) Calculate total price = nights × nightlyRate
    public BigDecimal calculatePrice(Long villaId, LocalDate start, LocalDate end) {
        Villa villa = villaRepo.findById(villaId)
                .orElseThrow(() -> new RuntimeException("Villa not found"));
        long nights = ChronoUnit.DAYS.between(start, end);
        return villa.getNightlyRate().multiply(BigDecimal.valueOf(nights));
    }

    // 4) Create a booking record if dates are still available
    @Transactional
    public Booking bookVilla(Long villaId, LocalDate start, LocalDate end) {
        if (!isDateRangeAvailable(villaId, start, end)) {
            throw new RuntimeException("Dates not available");
        }
        Villa villa = villaRepo.findById(villaId)
                .orElseThrow(() -> new RuntimeException("Villa not found"));
        Booking b = new Booking();
        b.setVilla(villa);
        b.setStartDate(start);
        b.setEndDate(end);
        return bookingRepo.save(b);
    }
}
