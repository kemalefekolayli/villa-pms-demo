
package io.villapms.villapms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PropertyCreateDto {
    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private Integer size;

    @NotNull(message = "Number of beds is required")
    @Positive(message = "Number of beds must be positive")
    private Integer bedNum;

    @NotNull(message = "Person capacity is required")
    @Positive(message = "Person capacity must be positive")
    private Integer personSize;

    @NotBlank(message = "Property address is required")
    private String propertyAddress;

    private Boolean animalsAllowed = false;

    @Positive(message = "Nightly rate must be positive")
    private Integer nightlyRate;
}
