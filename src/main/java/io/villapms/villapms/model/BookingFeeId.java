// BookingFeeId.java
package io.villapms.villapms.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class BookingFeeId implements Serializable {
    private Long bookingId;
    private Long feeId;
}
