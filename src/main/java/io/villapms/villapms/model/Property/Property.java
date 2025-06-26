// Property.java (replaces Villa.java)
package io.villapms.villapms.model.Property;

import io.villapms.villapms.model.Booking.Booking;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "nightly_rate")
    private Integer nightlyRate;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer size;

    @Column(name = "bed_num", nullable = false)
    private Integer bedNum;

    @Column(name = "person_size", nullable = false)
    private Integer personSize;

    @Column(name = "property_address", nullable = false)
    private String propertyAddress;

    @Column(name = "animals_allowed")
    private Boolean animalsAllowed;

    // Standard fee fields (instead of complex fee system)
    @Column(name = "cleaning_fee")
    private Integer cleaningFee;

    @Column(name = "security_deposit")
    private Integer securityDeposit;

    @Column(name = "service_fee_rate")
    private BigDecimal serviceFeeRate; // Percentage

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== RELATIONSHIPS ==========

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    // Categories relationship
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_category",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Features relationship
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_features",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Features> features = new HashSet<>();

    // Rules relationship
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_rules",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private Set<Rules> rules = new HashSet<>();

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
