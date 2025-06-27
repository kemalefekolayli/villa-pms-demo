
// Favorite.java
package io.villapms.villapms.model.User;

import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.model.User.FavoriteId;
import jakarta.persistence.*;
import lombok.Data;
import io.villapms.villapms.model.User.UserAccount;

@Entity
@Table(name = "favorite")
@Data
public class Favorite {


    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne
    @MapsId("propertyId")
    @JoinColumn(name = "property_id")
    private Property property;
}