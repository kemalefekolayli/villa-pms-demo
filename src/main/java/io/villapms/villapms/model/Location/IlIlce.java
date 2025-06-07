
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

    @Column(name = "il_id", nullable = false)
    private Long ilId; // references il.id

    @Column(name = "ililce_name", nullable = false)
    private String ililceName;
}
