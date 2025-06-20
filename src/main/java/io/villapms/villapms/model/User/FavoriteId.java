
package io.villapms.villapms.model.User;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class FavoriteId implements Serializable {
    private Long userId;
    private Long propertyId;
}