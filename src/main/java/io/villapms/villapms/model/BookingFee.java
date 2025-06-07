
package io.villapms.villapms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "booking_fees")
@Data
public class BookingFee {
    @EmbeddedId
    private BookingFeeId id;

    @ManyToOne
    @MapsId("bookingId")
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @MapsId("feeId")
    @JoinColumn(name = "fee_id")
    private OtherFee fee;

    @Column(name = "fee_amount", nullable = false)
    private Integer feeAmount;
}

