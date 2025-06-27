package io.villapms.villapms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PropertyCreateDto {
    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    // ========== VILLA MANAGEMENT ==========

    @NotNull(message = "Total villas is required")
    @Min(value = 1, message = "Must have at least 1 villa")
    private Integer totalVillas = 1;

    private String villaType; // "Standard", "Deluxe", "Presidential"

    private Boolean isMultiVilla = false; // Auto-calculated if totalVillas > 1

    // ========== VILLA SPECIFICATIONS (per villa) ==========

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private Integer size; // Size per villa in m²

    @NotNull(message = "Number of beds is required")
    @Positive(message = "Number of beds must be positive")
    private Integer bedNum; // Beds per villa

    @NotNull(message = "Person capacity is required")
    @Positive(message = "Person capacity must be positive")
    private Integer personSize; // Max occupants per villa

    @NotBlank(message = "Property address is required")
    private String propertyAddress;

    private Boolean animalsAllowed = false;

    // ========== PRICING (per villa) ==========

    @Positive(message = "Nightly rate must be positive")
    private Integer nightlyRate; // Per villa per night

    @Positive(message = "Cleaning fee must be positive")
    private Integer cleaningFee; // Per villa

    @Positive(message = "Security deposit must be positive")
    private Integer securityDeposit; // Per villa
}