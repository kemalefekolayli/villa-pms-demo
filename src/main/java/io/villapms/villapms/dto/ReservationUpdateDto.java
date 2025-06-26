
package io.villapms.villapms.dto;

import io.villapms.villapms.model.Booking.BookingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationUpdateDto {
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer nightlyRate;
    private BookingStatus status;
}