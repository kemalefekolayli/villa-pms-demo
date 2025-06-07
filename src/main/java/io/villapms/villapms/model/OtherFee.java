
package io.villapms.villapms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "other_fees")
@Data
public class OtherFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fee_name", nullable = false)
    private String feeName;

    @Column(name = "fee_description")
    private String feeDescription;
}
