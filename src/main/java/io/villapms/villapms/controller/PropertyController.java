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
    public List<Booking> getBookings(@PathVariable Long propertyId) {
        return availSvc.getAllBookings(propertyId);
    }

    @PostMapping("/{propertyId}/quote")
    public Map<String, Object> getQuote(
            @PathVariable Long propertyId,
            @RequestBody Map<String, String> payload
    ) {
        LocalDate start = LocalDate.parse(payload.get("startDate"));
        LocalDate end = LocalDate.parse(payload.get("endDate"));

        boolean available = availSvc.isDateRangeAvailable(propertyId, start, end);
        if (!available) {
            return Map.of("available", false);
        }
        BigDecimal totalPrice = availSvc.calculatePrice(propertyId, start, end);
        return Map.of(
                "available", true,
                "totalPrice", totalPrice
        );
    }

    @PostMapping("/{propertyId}/book")
    public ResponseEntity<?> book(
            @PathVariable Long propertyId,
            @RequestBody Map<String, String> payload
    ) {
        LocalDate start = LocalDate.parse(payload.get("startDate"));
        LocalDate end = LocalDate.parse(payload.get("endDate"));
        // TODO: Get userId from authentication context
        Long userId = 1L; // Temporary placeholder

        if (!availSvc.isDateRangeAvailable(propertyId, start, end)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Dates not available"));
        }

        Booking booking = availSvc.bookProperty(propertyId, start, end, userId);
        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId(),
                "startDate", booking.getCheckinDate(),
                "endDate", booking.getCheckoutDate()
        ));
    }
}