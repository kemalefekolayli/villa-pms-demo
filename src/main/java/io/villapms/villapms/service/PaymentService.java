// PaymentService.java (tam implementation)
package io.villapms.villapms.service;

import io.villapms.villapms.dto.PaymentCreateDto;
import io.villapms.villapms.dto.PaymentUpdateDto;
import io.villapms.villapms.model.Booking.Booking;
import io.villapms.villapms.model.Payment.Payment;
import io.villapms.villapms.model.Payment.PaymentStatus;
import io.villapms.villapms.repository.BookingRepository;
import io.villapms.villapms.repository.PaymentRepository;
import io.villapms.villapms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public Payment createPayment(PaymentCreateDto paymentDto) {
        // Booking'in var olduğunu kontrol et
        Booking booking = bookingRepository.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + paymentDto.getBookingId()));

        // User'ın var olduğunu kontrol et
        userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + booking.getUserId()));

        // Booking'in cancelled olmadığını kontrol et
        if (booking.getStatus().name().equals("CANCELLED")) {
            throw new RuntimeException("Cannot create payment for cancelled booking");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentDto.getAmount());
        payment.setMethod(paymentDto.getMethod());
        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setDescription(paymentDto.getDescription());
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        // Payment başarılı olursa booking'i confirm et (opsiyonel business logic)
        if (savedPayment.getStatus() == PaymentStatus.COMPLETED) {
            booking.setStatus(io.villapms.villapms.model.Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }

        return savedPayment;
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        // Booking'in var olduğunu kontrol et
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        return paymentRepository.findByBookingId(bookingId);
    }

    public List<Payment> getPaymentsByUserId(Long userId) {
        // User'ın var olduğunu kontrol et
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return paymentRepository.findByBookingUserId(userId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Payment updatePayment(Long id, PaymentUpdateDto updateDto) {
        Payment payment = getPaymentById(id);

        // Completed veya Failed payment'lar güncellenemez
        if (payment.getStatus() == PaymentStatus.COMPLETED ||
                payment.getStatus() == PaymentStatus.FAILED) {
            throw new RuntimeException("Cannot update completed or failed payments");
        }

        if (updateDto.getAmount() != null) {
            payment.setAmount(updateDto.getAmount());
        }
        if (updateDto.getStatus() != null) {
            PaymentStatus oldStatus = payment.getStatus();
            payment.setStatus(updateDto.getStatus());

            // Status değişikliği business logic'i
            handleStatusChange(payment, oldStatus, updateDto.getStatus());
        }
        if (updateDto.getMethod() != null) {
            payment.setMethod(updateDto.getMethod());
        }
        if (updateDto.getTransactionId() != null) {
            payment.setTransactionId(updateDto.getTransactionId());
        }
        if (updateDto.getDescription() != null) {
            payment.setDescription(updateDto.getDescription());
        }

        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);

        // Sadece PENDING veya FAILED payment'lar silinebilir
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Cannot delete completed payment");
        }

        paymentRepository.delete(payment);
    }

    public Payment processPayment(Long id) {
        Payment payment = getPaymentById(id);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Only pending payments can be processed");
        }

        // Simulate payment processing
        try {
            // Bu kısımda gerçek payment gateway entegrasyonu olur
            boolean paymentSuccess = simulatePaymentGateway(payment);

            if (paymentSuccess) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());

                // Booking'i confirm et
                Booking booking = payment.getBooking();
                booking.setStatus(io.villapms.villapms.model.Booking.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }

            return paymentRepository.save(payment);

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    public Payment refundPayment(Long id, String reason) {
        Payment payment = getPaymentById(id);

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        // Refund business logic
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setDescription(payment.getDescription() + " | Refund reason: " + reason);

        // Booking'i cancel et
        Booking booking = payment.getBooking();
        booking.setStatus(io.villapms.villapms.model.Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    // Private helper methods
    private void handleStatusChange(Payment payment, PaymentStatus oldStatus, PaymentStatus newStatus) {
        if (newStatus == PaymentStatus.COMPLETED && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        // Status değişiminde additional business logic
        if (newStatus == PaymentStatus.COMPLETED) {
            // Booking'i confirm et
            Booking booking = payment.getBooking();
            booking.setStatus(io.villapms.villapms.model.Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        } else if (newStatus == PaymentStatus.FAILED || newStatus == PaymentStatus.CANCELLED) {
            // Booking'i cancel et (opsiyonel)
            Booking booking = payment.getBooking();
            if (booking.getStatus() == io.villapms.villapms.model.Booking.BookingStatus.PENDING) {
                booking.setStatus(io.villapms.villapms.model.Booking.BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        }
    }

    private boolean simulatePaymentGateway(Payment payment) {
        // Gerçek payment gateway entegrasyonu burada olur
        // Şimdilik simulation
        try {
            Thread.sleep(1000); // Simulate network delay
            return payment.getAmount().doubleValue() > 0; // Simple success condition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // Utility methods
    public boolean isPaymentCompleted(Long bookingId) {
        List<Payment> payments = getPaymentsByBookingId(bookingId);
        return payments.stream()
                .anyMatch(payment -> payment.getStatus() == PaymentStatus.COMPLETED);
    }

    public boolean hasFailedPayments(Long bookingId) {
        List<Payment> payments = getPaymentsByBookingId(bookingId);
        return payments.stream()
                .anyMatch(payment -> payment.getStatus() == PaymentStatus.FAILED);
    }
}