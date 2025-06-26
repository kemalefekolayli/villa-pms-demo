// PropertyRules.java
package io.villapms.villapms.model.Property.junction;

import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.model.Property.Rules;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "property_rules")
@Data
public class PropertyRules {
    @EmbeddedId
    private PropertyRulesId id;

    @ManyToOne
    @MapsId("propertyId")
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @MapsId("ruleId")
    @JoinColumn(name = "rule_id")
    private Rules rule;
}