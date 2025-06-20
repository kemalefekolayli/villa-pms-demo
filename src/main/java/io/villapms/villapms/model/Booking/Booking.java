
// Booking.java (updated)
package io.villapms.villapms.model.Booking;

import io.villapms.villapms.model.Property.Property;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "user_id", nullable = false)
    private Long userId; // references your existing user system

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "checkout_date", nullable = false)
    private LocalDate checkoutDate;

    @Column(name = "nightly_rate", nullable = false)
    private Integer nightlyRate; // price per night in cents

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingFee> bookingFees;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingGuest> bookingGuests;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Compatibility methods for existing code
    public LocalDate getStartDate() {
        return checkinDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.checkinDate = startDate;
    }

    public LocalDate getEndDate() {
        return checkoutDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.checkoutDate = endDate;
    }

    public Property getVilla() {
        return property;
    }

    public void setVilla(Property property) {
        this.property = property;
    }
}