package com.fintech.platform.upi.payments.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, UUID>{
	
	Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey);
	
	// Add this inside your OrderRepository interface
    List<PaymentOrder> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
