
// PropertyCategory.java
package io.villapms.villapms.model.property.junction;

import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.model.Property.Category;
import io.villapms.villapms.model.Property.junction.PropertyCategoryId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "property_category")
@Data
public class PropertyCategory {
    @EmbeddedId
    private PropertyCategoryId id;

    @ManyToOne
    @MapsId("propertyId")
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;
}
