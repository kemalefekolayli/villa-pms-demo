package io.villapms.villapms.dto;

import io.villapms.villapms.model.Identity.IdentityType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IdentityUpdateDto {
    private String name;
    private String surname;
    private String phone;
    private String identityNumber;
    private String passportNumber;
    private IdentityType type;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String issuingAuthority;
}