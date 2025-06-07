
// PropertyFeatures.java
package io.villapms.villapms.model.Property.junction;

import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.model.Property.Features;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "property_features")
@Data
public class PropertyFeatures {
    @EmbeddedId
    private PropertyFeaturesId id;

    @ManyToOne
    @MapsId("featureId")
    @JoinColumn(name = "feature_id")
    private Features feature;

    @ManyToOne
    @MapsId("propertyId")
    @JoinColumn(name = "property_id")
    private Property property;
}
