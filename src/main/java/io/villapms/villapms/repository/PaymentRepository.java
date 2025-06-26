// PaymentRepository.java
package io.villapms.villapms.repository;

import io.villapms.villapms.model.Payment.Payment;
import io.villapms.villapms.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByBookingUserId(Long userId); // User'ın ödemeleri

    List<Payment> findByStatus(PaymentStatus status); // Status'e göre
}