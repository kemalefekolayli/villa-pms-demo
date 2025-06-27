// Booking.java (Multi-Villa Support)
package io.villapms.villapms.model.Booking;

import io.villapms.villapms.model.Property.Property;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Long userId;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "checkout_date", nullable = false)
    private LocalDate checkoutDate;

    // ========== VILLA QUANTITY MANAGEMENT ==========

    @Column(name = "villas_booked", nullable = false)
    private Integer villasBooked = 1; // Kaç villa kiralandı

    @Column(name = "villa_numbers")
    private String villNumbers; // "1,3,7" - Atanan villa numaraları (opsiyonel)

    @Column(name = "nightly_rate", nullable = false)
    private Integer nightlyRate; // Villa başına gecelik fiyat

    // Guest management (toplam için)
    @Column(name = "total_guests", nullable = false)
    private Integer totalGuests;

    @Column(name = "adults")
    private Integer adults;

    @Column(name = "children")
    private Integer children;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== BUSINESS METHODS ==========

    public Integer getTotalNightlyRate() {
        // Toplam gecelik fiyat (tüm villalar için)
        return nightlyRate * villasBooked;
    }

    public boolean isMultiVillaBooking() {
        return villasBooked > 1;
    }

    public Integer getGuestsPerVilla() {
        if (villasBooked <= 0) return totalGuests;
        return (int) Math.ceil((double) totalGuests / villasBooked);
    }

    public boolean exceedsVillaCapacity() {
        if (property == null) return false;
        return getGuestsPerVilla() > property.getPersonSize();
    }


    public LocalDate getStartDate() { return checkinDate; }
    public void setStartDate(LocalDate startDate) { this.checkinDate = startDate; }
    public LocalDate getEndDate() { return checkoutDate; }
    public void setEndDate(LocalDate endDate) { this.checkoutDate = endDate; }
    public Property getVilla() { return property; }
    public void setVilla(Property property) { this.property = property; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Default values
        if (villasBooked == null || villasBooked < 1) {
            villasBooked = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}