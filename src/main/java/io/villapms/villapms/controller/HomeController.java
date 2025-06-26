package io.villapms.villapms.controller;

import io.villapms.villapms.dto.PropertyCreateDto;
import io.villapms.villapms.dto.PropertyUpdateDto;
import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homes")
public class HomeController {

    private final PropertyService propertyService;

    public HomeController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createHome(@Valid @RequestBody PropertyCreateDto propertyDto) {
        try {
            Property home = propertyService.createProperty(propertyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Home created successfully",
                    "homeId", home.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getHomeById(@PathVariable Long id) {
        try {
            Property home = propertyService.getPropertyById(id);
            return ResponseEntity.ok(home);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Property>> getAllHomes(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Integer minSize,
            @RequestParam(required = false) Integer maxSize,
            @RequestParam(required = false) Boolean animalsAllowed) {

        List<Property> homes = propertyService.searchProperties(locationId, minSize, maxSize, animalsAllowed);
        return ResponseEntity.ok(homes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHome(
            @PathVariable Long id,
            @Valid @RequestBody PropertyUpdateDto updateDto) {
        try {
            Property updatedHome = propertyService.updateProperty(id, updateDto);
            return ResponseEntity.ok(Map.of(
                    "message", "Home updated successfully",
                    "homeId", updatedHome.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHome(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Home deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}