// VillaRepository.java
package io.villapms.villapms.repository;

import io.villapms.villapms.model.Property.Property;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
