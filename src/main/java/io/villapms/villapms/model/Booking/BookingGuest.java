
// BookingGuest.java
package io.villapms.villapms.model.Booking;

import io.villapms.villapms.model.User.GuestType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "booking_guests")
@Data
public class BookingGuest {
    @EmbeddedId
    private BookingGuestId id;

    @ManyToOne
    @MapsId("bookingId")
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @MapsId("guestTypeId")
    @JoinColumn(name = "guest_type_id")
    private GuestType guestType;

    @Column(name = "total_guests")
    private Integer totalGuests;
}

