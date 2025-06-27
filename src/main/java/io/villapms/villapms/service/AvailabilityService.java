// AvailabilityService.java (Multi-Villa Support)
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
import java.util.Map;

@Service
public class AvailabilityService {
    private final BookingRepository bookingRepo;
    private final PropertyRepository propertyRepo;

    public AvailabilityService(BookingRepository bookingRepo, PropertyRepository propertyRepo) {
        this.bookingRepo = bookingRepo;
        this.propertyRepo = propertyRepo;
    }

    // ========== AVAILABILITY CHECKING ==========

    public List<Booking> getAllBookings(Long propertyId) {
        return bookingRepo.findByPropertyIdOrderByCheckinDateAsc(propertyId);
    }

    /**
     * Check how many villas are available for given date range
     */
    public Integer getAvailableVillaCount(Long propertyId, LocalDate start, LocalDate end) {
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Get overlapping bookings
        List<Booking> overlappingBookings = bookingRepo.findOverlapping(propertyId, start, end);

        // Calculate total booked villas
        Integer totalBookedVillas = overlappingBookings.stream()
                .filter(booking -> !booking.getStatus().name().equals("CANCELLED"))
                .mapToInt(Booking::getVillasBooked)
                .sum();

        // Available = Total - Booked
        return property.getTotalVillas() - totalBookedVillas;
    }

    /**
     * Check if specific number of villas are available
     */
    public boolean areVillasAvailable(Long propertyId, LocalDate start, LocalDate end, Integer requestedVillas) {
        Integer availableCount = getAvailableVillaCount(propertyId, start, end);
        return availableCount >= requestedVillas;
    }

    /**
     * Legacy method - checks if at least 1 villa is available
     */
    public boolean isDateRangeAvailable(Long propertyId, LocalDate start, LocalDate end) {
        return areVillasAvailable(propertyId, start, end, 1);
    }

    // ========== PRICING CALCULATIONS ==========

    /**
     * Calculate price for specific number of villas
     */
    public BigDecimal calculatePrice(Long propertyId, LocalDate start, LocalDate end, Integer villasRequested) {
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        long nights = ChronoUnit.DAYS.between(start, end);
        BigDecimal nightlyRatePerVilla = BigDecimal.valueOf(property.getNightlyRate());
        BigDecimal totalNightlyRate = nightlyRatePerVilla.multiply(BigDecimal.valueOf(villasRequested));

        return totalNightlyRate.multiply(BigDecimal.valueOf(nights));
    }

    /**
     * Legacy method - calculates price for 1 villa
     */
    public BigDecimal calculatePrice(Long propertyId, LocalDate start, LocalDate end) {
        return calculatePrice(propertyId, start, end, 1);
    }

    /**
     * Get pricing breakdown with fees
     */
    public Map<String, Object> getPricingBreakdown(Long propertyId, LocalDate start, LocalDate end, Integer villasRequested) {
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        long nights = ChronoUnit.DAYS.between(start, end);

        BigDecimal nightlyRatePerVilla = BigDecimal.valueOf(property.getNightlyRate());
        BigDecimal subtotal = nightlyRatePerVilla
                .multiply(BigDecimal.valueOf(villasRequested))
                .multiply(BigDecimal.valueOf(nights));

        BigDecimal cleaningFee = property.getCleaningFee() != null ?
                BigDecimal.valueOf(property.getCleaningFee()).multiply(BigDecimal.valueOf(villasRequested)) :
                BigDecimal.ZERO;

        BigDecimal securityDeposit = property.getSecurityDeposit() != null ?
                BigDecimal.valueOf(property.getSecurityDeposit()).multiply(BigDecimal.valueOf(villasRequested)) :
                BigDecimal.ZERO;

        BigDecimal serviceFee = property.getServiceFeeRate() != null ?
                subtotal.multiply(property.getServiceFeeRate()).divide(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

        BigDecimal total = subtotal.add(cleaningFee).add(serviceFee);

        return Map.of(
                "villasRequested", villasRequested,
                "nights", nights,
                "nightlyRatePerVilla", nightlyRatePerVilla,
                "subtotal", subtotal,
                "cleaningFee", cleaningFee,
                "serviceFee", serviceFee,
                "securityDeposit", securityDeposit,
                "total", total
        );
    }

    // ========== BOOKING OPERATIONS ==========

    @Transactional
    public Booking bookProperty(Long propertyId, LocalDate start, LocalDate end, Long userId, Integer villasRequested) {
        // Validate input
        if (villasRequested == null || villasRequested < 1) {
            villasRequested = 1;
        }

        // Check availability
        if (!areVillasAvailable(propertyId, start, end, villasRequested)) {
            Integer available = getAvailableVillaCount(propertyId, start, end);
            throw new RuntimeException(String.format(
                    "Only %d villas available, but %d requested", available, villasRequested));
        }

        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setCheckinDate(start);
        booking.setCheckoutDate(end);
        booking.setUserId(userId);
        booking.setVillasBooked(villasRequested);
        booking.setNightlyRate(property.getNightlyRate());

        return bookingRepo.save(booking);
    }

    /**
     * Legacy method - books 1 villa
     */
    @Transactional
    public Booking bookProperty(Long propertyId, LocalDate start, LocalDate end, Long userId) {
        return bookProperty(propertyId, start, end, userId, 1);
    }

    // ========== AVAILABILITY SUMMARY ==========

    public Map<String, Object> getAvailabilitySummary(Long propertyId, LocalDate start, LocalDate end) {
        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Integer availableVillas = getAvailableVillaCount(propertyId, start, end);
        Integer totalVillas = property.getTotalVillas();
        Integer bookedVillas = totalVillas - availableVillas;

        List<Booking> overlappingBookings = bookingRepo.findOverlapping(propertyId, start, end);
        overlappingBookings.removeIf(booking -> booking.getStatus().name().equals("CANCELLED"));

        return Map.of(
                "propertyId", propertyId,
                "propertyName", property.getName(),
                "isMultiVilla", property.getIsMultiVilla(),
                "totalVillas", totalVillas,
                "availableVillas", availableVillas,
                "bookedVillas", bookedVillas,
                "isFullyBooked", availableVillas == 0,
                "hasAvailability", availableVillas > 0,
                "overlappingBookings", overlappingBookings.size()
        );
    }


    public Map<Long, Map<String, Object>> getMultipleAvailability(List<Long> propertyIds, LocalDate start, LocalDate end) {
        return propertyIds.stream()
                .collect(java.util.stream.Collectors.toMap(
                        propertyId -> propertyId,
                        propertyId -> {
                            try {
                                return getAvailabilitySummary(propertyId, start, end);
                            } catch (Exception e) {
                                return Map.of("error", e.getMessage());
                            }
                        }
                ));
    }
}