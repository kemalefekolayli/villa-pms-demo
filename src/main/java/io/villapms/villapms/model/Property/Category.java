
package io.villapms.villapms.model.Property;

import jakarta.persistence.*;
        import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    // Back reference
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Property> properties = new HashSet<>();
}