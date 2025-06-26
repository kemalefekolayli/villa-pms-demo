
package io.villapms.villapms.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {
    private String name;
    private String surname;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;
    private LocalDate birthday;
}
