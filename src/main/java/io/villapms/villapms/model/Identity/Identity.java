package io.villapms.villapms.model.Identity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "identity")
@Data
public class Identity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String phone;

    @Column(name = "identity_number", unique = true)
    private String identityNumber; // National ID or passport number

    @Column(name = "passport_number", unique = true)
    private String passportNumber;

    @Enumerated(EnumType.STRING)
    private IdentityType type = IdentityType.NATIONAL_ID;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "user_id")
    private Long userId; // references user_account.id

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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