package io.villapms.villapms.model.Property.junction;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class PropertyFeaturesId implements Serializable {
    private Long featureId;
    private Long propertyId;
}