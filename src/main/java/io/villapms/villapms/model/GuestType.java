
package io.villapms.villapms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "guest_type")
@Data
public class GuestType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_type_name", nullable = false, unique = true)
    private String guestTypeName;

    @Column(name = "guest_description")
    private String guestDescription;
}