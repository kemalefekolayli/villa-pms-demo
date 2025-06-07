
// PropertyCategoryId.java
package io.villapms.villapms.model.Property.junction;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class PropertyCategoryId implements Serializable {
    private Long propertyId;
    private Long categoryId;
}