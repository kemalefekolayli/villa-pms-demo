
package io.villapms.villapms.model.Location;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "il_ilce")
@Data
public class IlIlce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "province_name", nullable = false)
    private String provinceName;

    @Column(name = "district_name", nullable = false)
    private String districtName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @PrePersist
    @PreUpdate
    private void updateFullName() {
        this.fullName = provinceName + " / " + districtName;
    }
}