
package io.villapms.villapms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}