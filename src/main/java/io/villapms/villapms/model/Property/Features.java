
// Features.java
package io.villapms.villapms.model.Property;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "features")
@Data
public class Features {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feature_name", nullable = false, unique = true)
    private String featureName;

    @Column(name = "feature_description")
    private String featureDescription;

    // Back reference
    @ManyToMany(mappedBy = "features", fetch = FetchType.LAZY)
    private Set<Property> properties = new HashSet<>();
}