package io.villapms.villapms.controller;

import io.villapms.villapms.dto.PaymentCreateDto;
import io.villapms.villapms.dto.PaymentUpdateDto;
import io.villapms.villapms.model.Payment.Payment;
import io.villapms.villapms.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@Valid @RequestBody PaymentCreateDto paymentDto) {
        try {
            Payment payment = paymentService.createPayment(paymentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Payment created successfully",
                    "paymentId", payment.getId(),
                    "status", payment.getStatus()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentUpdateDto updateDto) {
        try {
            Payment updatedPayment = paymentService.updatePayment(id, updateDto);
            return ResponseEntity.ok(Map.of(
                    "message", "Payment updated successfully",
                    "paymentId", updatedPayment.getId(),
                    "status", updatedPayment.getStatus()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Payment deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}