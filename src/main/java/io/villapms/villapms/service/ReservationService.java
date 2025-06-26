package io.villapms.villapms.service;

import io.villapms.villapms.dto.ReservationCreateDto;
import io.villapms.villapms.dto.ReservationUpdateDto;
import io.villapms.villapms.model.Booking.Booking;
import io.villapms.villapms.model.Booking.BookingStatus;
import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.repository.BookingRepository;
import io.villapms.villapms.repository.PropertyRepository;
import io.villapms.villapms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;

    public ReservationService(BookingRepository bookingRepository,
                              PropertyRepository propertyRepository,
                              UserRepository userRepository,
                              AvailabilityService availabilityService) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
    }

    public Booking createReservation(ReservationCreateDto reservationDto) {
        // Validate dates
        if (!reservationDto.getCheckinDate().isBefore(reservationDto.getCheckoutDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        // Check if property exists
        Property property = propertyRepository.findById(reservationDto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + reservationDto.getPropertyId()));

        // Check if user exists
        userRepository.findById(reservationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + reservationDto.getUserId()));

        // Check availability
        if (!availabilityService.isDateRangeAvailable(
                reservationDto.getPropertyId(),
                reservationDto.getCheckinDate(),
                reservationDto.getCheckoutDate())) {
            throw new RuntimeException("Property is not available for the selected dates");
        }

        Booking reservation = new Booking();
        reservation.setProperty(property);
        reservation.setUserId(reservationDto.getUserId());
        reservation.setCheckinDate(reservationDto.getCheckinDate());
        reservation.setCheckoutDate(reservationDto.getCheckoutDate());
        reservation.setNightlyRate(reservationDto.getNightlyRate() != null ?
                reservationDto.getNightlyRate() : property.getNightlyRate());
        reservation.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(reservation);
    }

    public Booking getReservationById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
    }

    public List<Booking> getAllReservations() {
        return bookingRepository.findAll();
    }

    public List<Booking> getReservationsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getReservationsByPropertyId(Long propertyId) {
        return bookingRepository.findByPropertyIdOrderByCheckinDateAsc(propertyId);
    }

    public List<Booking> getReservationsByUserIdAndPropertyId(Long userId, Long propertyId) {
        return bookingRepository.findByPropertyIdAndUserId(propertyId, userId);
    }

    public Booking updateReservation(Long id, ReservationUpdateDto updateDto) {
        Booking reservation = getReservationById(id);

        // If dates are being updated, check availability
        if (updateDto.getCheckinDate() != null || updateDto.getCheckoutDate() != null) {
            LocalDate newCheckinDate = updateDto.getCheckinDate() != null ?
                    updateDto.getCheckinDate() : reservation.getCheckinDate();
            LocalDate newCheckoutDate = updateDto.getCheckoutDate() != null ?
                    updateDto.getCheckoutDate() : reservation.getCheckoutDate();

            if (!newCheckinDate.isBefore(newCheckoutDate)) {
                throw new RuntimeException("Check-out date must be after check-in date");
            }

            // Check availability (exclude current reservation)
            List<Booking> overlaps = bookingRepository.findOverlapping(
                    reservation.getProperty().getId(), newCheckinDate, newCheckoutDate);
            overlaps.removeIf(booking -> booking.getId().equals(id));

            if (!overlaps.isEmpty()) {
                throw new RuntimeException("Property is not available for the selected dates");
            }

            reservation.setCheckinDate(newCheckinDate);
            reservation.setCheckoutDate(newCheckoutDate);
        }

        if (updateDto.getNightlyRate() != null) {
            reservation.setNightlyRate(updateDto.getNightlyRate());
        }

        if (updateDto.getStatus() != null) {
            reservation.setStatus(updateDto.getStatus());
        }

        return bookingRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        Booking reservation = getReservationById(id);
        bookingRepository.delete(reservation);
    }

    public void cancelReservation(Long id) {
        Booking reservation = getReservationById(id);
        reservation.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(reservation);
    }

    public void confirmReservation(Long id) {
        Booking reservation = getReservationById(id);
        reservation.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(reservation);
    }
}