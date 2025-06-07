package io.villapms.villapms.model.Property.junction;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class PropertyRulesId implements Serializable {
    private Long propertyId;
    private Long ruleId;
}