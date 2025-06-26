package io.villapms.villapms.service;

import io.villapms.villapms.dto.PropertyCreateDto;
import io.villapms.villapms.dto.PropertyUpdateDto;
import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.repository.PropertyRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property createProperty(PropertyCreateDto propertyDto) {
        Property property = new Property();
        property.setLocationId(propertyDto.getLocationId());
        property.setName(propertyDto.getName());
        property.setDescription(propertyDto.getDescription());
        property.setSize(propertyDto.getSize());
        property.setBedNum(propertyDto.getBedNum());
        property.setPersonSize(propertyDto.getPersonSize());
        property.setPropertyAddress(propertyDto.getPropertyAddress());
        property.setAnimalsAllowed(propertyDto.getAnimalsAllowed());
        property.setNightlyRate(propertyDto.getNightlyRate());

        return propertyRepository.save(property);
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> searchProperties(Long locationId, Integer minSize, Integer maxSize, Boolean animalsAllowed) {
        return propertyRepository.findAll(Specification.where(
                        locationId != null ? hasLocationId(locationId) : null)
                .and(minSize != null ? hasSizeGreaterThanOrEqual(minSize) : null)
                .and(maxSize != null ? hasSizeLessThanOrEqual(maxSize) : null)
                .and(animalsAllowed != null ? hasAnimalsAllowed(animalsAllowed) : null));
    }

    public Property updateProperty(Long id, PropertyUpdateDto updateDto) {
        Property property = getPropertyById(id);

        if (updateDto.getLocationId() != null) {
            property.setLocationId(updateDto.getLocationId());
        }
        if (updateDto.getName() != null) {
            property.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            property.setDescription(updateDto.getDescription());
        }
        if (updateDto.getSize() != null) {
            property.setSize(updateDto.getSize());
        }
        if (updateDto.getBedNum() != null) {
            property.setBedNum(updateDto.getBedNum());
        }
        if (updateDto.getPersonSize() != null) {
            property.setPersonSize(updateDto.getPersonSize());
        }
        if (updateDto.getPropertyAddress() != null) {
            property.setPropertyAddress(updateDto.getPropertyAddress());
        }
        if (updateDto.getAnimalsAllowed() != null) {
            property.setAnimalsAllowed(updateDto.getAnimalsAllowed());
        }
        if (updateDto.getNightlyRate() != null) {
            property.setNightlyRate(updateDto.getNightlyRate());
        }

        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        Property property = getPropertyById(id);
        propertyRepository.delete(property);
    }

    // Specification methods for dynamic queries
    private Specification<Property> hasLocationId(Long locationId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("locationId"), locationId);
    }

    private Specification<Property> hasSizeGreaterThanOrEqual(Integer minSize) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("size"), minSize);
    }

    private Specification<Property> hasSizeLessThanOrEqual(Integer maxSize) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("size"), maxSize);
    }

    private Specification<Property> hasAnimalsAllowed(Boolean animalsAllowed) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("animalsAllowed"), animalsAllowed);
    }
}