package io.villapms.villapms.model.Location;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "il")
@Data
public class Il {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "il_name", nullable = false)
    private String ilName;
}
