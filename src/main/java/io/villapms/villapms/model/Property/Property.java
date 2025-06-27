// Property.java (Multi-Villa Support)
package io.villapms.villapms.model.Property;

import io.villapms.villapms.model.Booking.Booking;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "property")
@Data
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    // ========== VILLA INVENTORY MANAGEMENT ==========

    @Column(name = "total_villas", nullable = false)
    private Integer totalVillas = 1; // Default: tek villa

    @Column(name = "villa_type")
    private String villaType; // "Standard", "Deluxe", "Presidential" vs.

    @Column(name = "is_multi_villa")
    private Boolean isMultiVilla = false; // True ise villa kompleksi

    // Villa özellikleri (her villa için geçerli)
    @Column(nullable = false)
    private Integer size; // Her villa'nın m²'si

    @Column(name = "bed_num", nullable = false)
    private Integer bedNum; // Her villa'daki yatak sayısı

    @Column(name = "person_size", nullable = false)
    private Integer personSize; // Her villa'nın max kapasitesi

    @Column(name = "property_address", nullable = false)
    private String propertyAddress;

    @Column(name = "animals_allowed")
    private Boolean animalsAllowed;

    // Pricing (per villa)
    @Column(name = "nightly_rate")
    private Integer nightlyRate; // Villa başına gecelik fiyat

    @Column(name = "cleaning_fee")
    private Integer cleaningFee; // Villa başına temizlik ücreti

    @Column(name = "security_deposit")
    private Integer securityDeposit;

    @Column(name = "service_fee_rate")
    private BigDecimal serviceFeeRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== RELATIONSHIPS ==========

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_category",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_features",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Features> features = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_rules",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private Set<Rules> rules = new HashSet<>();

    // ========== BUSINESS METHODS ==========

    public boolean isSingleVilla() {
        return !isMultiVilla || totalVillas == 1;
    }

    public Integer getTotalCapacity() {
        return personSize * totalVillas; // Toplam kişi kapasitesi
    }

    public Integer getTotalSize() {
        return size * totalVillas; // Toplam alan
    }

    public BigDecimal getTotalNightlyRate() {
        // Eğer tüm villalar kiralanırsa toplam fiyat
        return BigDecimal.valueOf(nightlyRate).multiply(BigDecimal.valueOf(totalVillas));
    }

    // ========== OOP METHODS ==========

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getProperties().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getProperties().remove(this);
    }

    public void addFeature(Features feature) {
        this.features.add(feature);
    }

    public void removeFeature(Features feature) {
        this.features.remove(feature);
    }

    public void addRule(Rules rule) {
        this.rules.add(rule);
    }

    public void removeRule(Rules rule) {
        this.rules.remove(rule);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Multi-villa kontrolü
        if (totalVillas > 1) {
            isMultiVilla = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Multi-villa kontrolü
        if (totalVillas > 1) {
            isMultiVilla = true;
        } else {
            isMultiVilla = false;
        }
    }
}