// PropertyRepository.java (Fixed)
package io.villapms.villapms.repository;

import io.villapms.villapms.model.Property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    // JpaSpecificationExecutor adds methods like findAll(Specification<Property> spec)
}