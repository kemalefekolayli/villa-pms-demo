package io.villapms.villapms.model.Property;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rules")
@Data
public class Rules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "rule_description", nullable = false)
    private String ruleDescription;

    // Back reference
    @ManyToMany(mappedBy = "rules", fetch = FetchType.LAZY)
    private Set<Property> properties = new HashSet<>();
}