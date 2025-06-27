// PropertyService.java (Fixed)
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

        // Multi-villa fields
        property.setTotalVillas(propertyDto.getTotalVillas());
        property.setVillaType(propertyDto.getVillaType());
        property.setIsMultiVilla(propertyDto.getIsMultiVilla());

        // Villa specifications
        property.setSize(propertyDto.getSize());
        property.setBedNum(propertyDto.getBedNum());
        property.setPersonSize(propertyDto.getPersonSize());
        property.setPropertyAddress(propertyDto.getPropertyAddress());
        property.setAnimalsAllowed(propertyDto.getAnimalsAllowed());

        // Pricing
        property.setNightlyRate(propertyDto.getNightlyRate());
        property.setCleaningFee(propertyDto.getCleaningFee());
        property.setSecurityDeposit(propertyDto.getSecurityDeposit());

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
        // Build specification dynamically (modern approach)
        Specification<Property> spec = null;

        if (locationId != null) {
            spec = hasLocationId(locationId);
        }

        if (minSize != null) {
            spec = spec == null ? hasSizeGreaterThanOrEqual(minSize) : spec.and(hasSizeGreaterThanOrEqual(minSize));
        }

        if (maxSize != null) {
            spec = spec == null ? hasSizeLessThanOrEqual(maxSize) : spec.and(hasSizeLessThanOrEqual(maxSize));
        }

        if (animalsAllowed != null) {
            spec = spec == null ? hasAnimalsAllowed(animalsAllowed) : spec.and(hasAnimalsAllowed(animalsAllowed));
        }

        // If no filters, return all
        if (spec == null) {
            return propertyRepository.findAll();
        }

        return propertyRepository.findAll(spec);
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

        // Multi-villa fields
        if (updateDto.getTotalVillas() != null) {
            property.setTotalVillas(updateDto.getTotalVillas());
        }
        if (updateDto.getVillaType() != null) {
            property.setVillaType(updateDto.getVillaType());
        }
        if (updateDto.getIsMultiVilla() != null) {
            property.setIsMultiVilla(updateDto.getIsMultiVilla());
        }

        // Villa specifications
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

        // Pricing
        if (updateDto.getNightlyRate() != null) {
            property.setNightlyRate(updateDto.getNightlyRate());
        }
        if (updateDto.getCleaningFee() != null) {
            property.setCleaningFee(updateDto.getCleaningFee());
        }
        if (updateDto.getSecurityDeposit() != null) {
            property.setSecurityDeposit(updateDto.getSecurityDeposit());
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