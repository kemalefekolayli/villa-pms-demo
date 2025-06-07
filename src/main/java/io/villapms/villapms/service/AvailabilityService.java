package io.villapms.villapms.service;

import io.villapms.villapms.model.Booking.Booking;
import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.repository.BookingRepository;
import io.villapms.villapms.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AvailabilityService {
    private final BookingRepository bookingRepo;
    private final PropertyRepository propertyRepo;

    public AvailabilityService(BookingRepository bookingRepo, PropertyRepository propertyRepo) {
        this.bookingRepo = bookingRepo;
        this.propertyRepo = propertyRepo;
    }

    public List<Booking> getAllBookings(Long propertyId) {
        return bookingRepo.findByPropertyIdOrderByCheckinDateAsc(propertyId);
    }

    public boolean isDateRangeAvailable(Long propertyId, LocalDate start, LocalDate end) {
        List<Booking> overlaps = bookingRepo.findOverlapping(propertyId, start, end);
        return overlaps.isEmpty();
    }

    public BigDecimal calculatePrice(Long propertyId, LocalDate start, LocalDate end) {
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        long nights = ChronoUnit.DAYS.between(start, end);

        // Convert Integer nightlyRate to BigDecimal
        BigDecimal nightlyRate = BigDecimal.valueOf(property.getNightlyRate());
        return nightlyRate.multiply(BigDecimal.valueOf(nights));
    }

    @Transactional
    public Booking bookProperty(Long propertyId, LocalDate start, LocalDate end, Long userId) {
        if (!isDateRangeAvailable(propertyId, start, end)) {
            throw new RuntimeException("Dates not available");
        }
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setCheckinDate(start);
        booking.setCheckoutDate(end);
        booking.setUserId(userId);
        booking.setNightlyRate(property.getNightlyRate());

        return bookingRepo.save(booking);
    }
}