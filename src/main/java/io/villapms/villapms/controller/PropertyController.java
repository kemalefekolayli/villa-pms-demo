// PropertyController.java (Multi-Villa Support)
package io.villapms.villapms.controller;

import io.villapms.villapms.model.Booking.Booking;
import io.villapms.villapms.model.Property.Property;
import io.villapms.villapms.repository.PropertyRepository;
import io.villapms.villapms.service.AvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/properties")
public class PropertyController {
    private final AvailabilityService availSvc;
    private final PropertyRepository propRepo;

    public PropertyController(AvailabilityService availSvc, PropertyRepository propRepo) {
        this.availSvc = availSvc;
        this.propRepo = propRepo;
    }

    @GetMapping
    public List<Property> listProperties() {
        return propRepo.findAll();
    }

    @GetMapping("/{propertyId}/availability")
    public ResponseEntity<Map<String, Object>> getAvailability(
            @PathVariable Long propertyId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        try {
            Map<String, Object> availability = availSvc.getAvailabilitySummary(propertyId, startDate, endDate);
            return ResponseEntity.ok(availability);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{propertyId}/bookings")
    public List<Booking> getBookings(@PathVariable Long propertyId) {
        return availSvc.getAllBookings(propertyId);
    }

    @PostMapping("/{propertyId}/quote")
    public ResponseEntity<Map<String, Object>> getQuote(
            @PathVariable Long propertyId,
            @RequestBody Map<String, Object> payload) {

        try {
            LocalDate start = LocalDate.parse((String) payload.get("startDate"));
            LocalDate end = LocalDate.parse((String) payload.get("endDate"));
            Integer villasRequested = payload.get("villasRequested") != null ?
                    (Integer) payload.get("villasRequested") : 1;

            // Check availability
            Integer availableVillas = availSvc.getAvailableVillaCount(propertyId, start, end);

            if (availableVillas < villasRequested) {
                return ResponseEntity.ok(Map.of(
                        "available", false,
                        "requestedVillas", villasRequested,
                        "availableVillas", availableVillas,
                        "message", String.format("Only %d villas available, but %d requested",
                                availableVillas, villasRequested)
                ));
            }

            // Get pricing breakdown
            Map<String, Object> pricing = availSvc.getPricingBreakdown(propertyId, start, end, villasRequested);

            return ResponseEntity.ok(Map.of(
                    "available", true,
                    "requestedVillas", villasRequested,
                    "availableVillas", availableVillas,
                    "pricing", pricing
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Invalid request: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{propertyId}/book")
    public ResponseEntity<Map<String, Object>> book(
            @PathVariable Long propertyId,
            @RequestBody Map<String, Object> payload) {

        try {
            LocalDate start = LocalDate.parse((String) payload.get("startDate"));
            LocalDate end = LocalDate.parse((String) payload.get("endDate"));
            Integer villasRequested = payload.get("villasRequested") != null ?
                    (Integer) payload.get("villasRequested") : 1;

            // TODO: Get userId from authentication context
            Long userId = 1L; // Temporary placeholder

            // Validate villa count
            if (villasRequested < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "error", "Number of villas must be at least 1"
                ));
            }

            // Check availability
            if (!availSvc.areVillasAvailable(propertyId, start, end, villasRequested)) {
                Integer available = availSvc.getAvailableVillaCount(propertyId, start, end);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "error", String.format("Only %d villas available, but %d requested",
                                available, villasRequested),
                        "availableVillas", available,
                        "requestedVillas", villasRequested
                ));
            }

            Booking booking = availSvc.bookProperty(propertyId, start, end, userId, villasRequested);

            return ResponseEntity.ok(Map.of(
                    "bookingId", booking.getId(),
                    "startDate", booking.getCheckinDate(),
                    "endDate", booking.getCheckoutDate(),
                    "villasBooked", booking.getVillasBooked(),
                    "totalNightlyRate", booking.getTotalNightlyRate(),
                    "status", booking.getStatus()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // ========== SEARCH WITH AVAILABILITY ==========

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchWithAvailability(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Integer villasNeeded) {

        if (villasNeeded == null) villasNeeded = 1;

        try {
            List<Property> allProperties = propRepo.findAll();
            final Integer finalVillasNeeded = villasNeeded;

            List<Map<String, Object>> results = allProperties.stream()
                    .map(property -> {
                        Map<String, Object> result = Map.of(
                                "property", property,
                                "availability", availSvc.getAvailabilitySummary(property.getId(), startDate, endDate)
                        );
                        return result;
                    })
                    .filter(result -> {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> availability = (Map<String, Object>) result.get("availability");
                        Integer availableVillas = (Integer) availability.get("availableVillas");
                        return availableVillas >= finalVillasNeeded;
                    })
                    .toList();

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(Map.of(
                    "error", e.getMessage()
            )));
        }
    }

    // ========== PROPERTY DETAILS WITH AVAILABILITY ==========

    @GetMapping("/{propertyId}")
    public ResponseEntity<Map<String, Object>> getPropertyDetails(
            @PathVariable Long propertyId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        try {
            Property property = propRepo.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            Map<String, Object> response = Map.of(
                    "property", property
            );

            // Add availability info if dates provided
            if (startDate != null && endDate != null) {
                Map<String, Object> availability = availSvc.getAvailabilitySummary(propertyId, startDate, endDate);
                response = Map.of(
                        "property", property,
                        "availability", availability
                );
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}