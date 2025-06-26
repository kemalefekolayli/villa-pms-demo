package io.villapms.villapms.controller;

import io.villapms.villapms.dto.ReservationCreateDto;
import io.villapms.villapms.dto.ReservationUpdateDto;
import io.villapms.villapms.model.Booking.Booking;
import io.villapms.villapms.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(@Valid @RequestBody ReservationCreateDto reservationDto) {
        try {
            Booking reservation = reservationService.createReservation(reservationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Reservation created successfully",
                    "reservationId", reservation.getId(),
                    "status", reservation.getStatus()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getReservationById(@PathVariable Long id) {
        try {
            Booking reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long propertyId) {

        List<Booking> reservations;

        if (userId != null && propertyId != null) {
            reservations = reservationService.getReservationsByUserIdAndPropertyId(userId, propertyId);
        } else if (userId != null) {
            reservations = reservationService.getReservationsByUserId(userId);
        } else if (propertyId != null) {
            reservations = reservationService.getReservationsByPropertyId(propertyId);
        } else {
            reservations = reservationService.getAllReservations();
        }

        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateDto updateDto) {
        try {
            Booking updatedReservation = reservationService.updateReservation(id, updateDto);
            return ResponseEntity.ok(Map.of(
                    "message", "Reservation updated successfully",
                    "reservationId", updatedReservation.getId(),
                    "status", updatedReservation.getStatus()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Reservation deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Reservation cancelled successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, String>> confirmReservation(@PathVariable Long id) {
        try {
            reservationService.confirmReservation(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Reservation confirmed successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}