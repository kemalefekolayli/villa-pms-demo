// src/main/java/io/villapms/villapms/model/User/Favorite.java
package io.villapms.villapms.model.User;

import io.villapms.villapms.model.Property.Property;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorite")
@Data
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}