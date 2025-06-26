
package io.villapms.villapms.dto;

import io.villapms.villapms.model.Payment.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentUpdateDto {
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
}