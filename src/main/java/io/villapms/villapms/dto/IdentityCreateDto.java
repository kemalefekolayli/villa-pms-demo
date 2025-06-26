
package io.villapms.villapms.dto;

import io.villapms.villapms.model.Identity.IdentityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IdentityCreateDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String identityNumber;
    private String passportNumber;

    @NotNull(message = "Identity type is required")
    private IdentityType type;

    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String issuingAuthority;

    @NotNull(message = "User ID is required")
    private Long userId;
}

