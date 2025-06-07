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
@RequestMapping("/villas")
public class VillaController {
    private final AvailabilityService availSvc;
    private final PropertyRepository propRepo;

    public VillaController(AvailabilityService availSvc, PropertyRepository villaRepo) {
        this.availSvc = availSvc;
        this.propRepo = villaRepo;
    }

    // 1) List all villas (so frontend knows villa IDs & nightly rates)
    @GetMapping
    public List<Property> listVillas() {
        return propRepo.findAll();
    }

    // 2) Return existing bookings for one villa
    @GetMapping("/{villaId}/availability")
    public List<Booking> getBookings(@PathVariable Long villaId) {
        return availSvc.getAllBookings(villaId);
    }

    // 3) Calculate a price quote (and check availability) for given date‐range
    @PostMapping("/{villaId}/quote")
    public Map<String, Object> getQuote(
            @PathVariable Long villaId,
            @RequestBody Map<String, String> payload
    ) {
        LocalDate start = LocalDate.parse(payload.get("startDate"));
        LocalDate end   = LocalDate.parse(payload.get("endDate"));

        boolean available = availSvc.isDateRangeAvailable(villaId, start, end);
        if (!available) {
            return Map.of("available", false);
        }
        BigDecimal totalPrice = availSvc.calculatePrice(villaId, start, end);
        return Map.of(
                "available",  true,
                "totalPrice", totalPrice
        );
    }

    // 4) Create a booking if dates are still free
    @PostMapping("/{villaId}/book")
    public ResponseEntity<?> book(
            @PathVariable Long villaId,
            @RequestBody Map<String, String> payload
    ) {
        LocalDate start = LocalDate.parse(payload.get("startDate"));
        LocalDate end   = LocalDate.parse(payload.get("endDate"));

        if (!availSvc.isDateRangeAvailable(villaId, start, end)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Dates not available"));
        }

        Booking booking = availSvc.bookVilla(villaId, start, end);
        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId(),
                "startDate",  booking.getStartDate(),
                "endDate",    booking.getEndDate()
        ));
    }
}
