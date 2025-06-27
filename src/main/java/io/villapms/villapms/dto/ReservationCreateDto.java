
// ReservationCreateDto.java (Updated for Multi-Villa)
package io.villapms.villapms.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationCreateDto {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkinDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkoutDate;

    // ========== VILLA BOOKING ==========

    @NotNull(message = "Number of villas is required")
    @Min(value = 1, message = "Must book at least 1 villa")
    private Integer villasRequested = 1;

    // Guest information (total across all villas)
    @NotNull(message = "Total guests is required")
    @Min(value = 1, message = "Must have at least 1 guest")
    private Integer totalGuests;

    private Integer adults;
    private Integer children;

    private Integer nightlyRate; // Optional, will use property's default if not provided
}