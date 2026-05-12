package com.fintech.platform.upi.payments.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionAttemptRepository extends JpaRepository<TransactionAttempt, UUID>{
	
	Optional<TransactionAttempt> findByGatewayReferenceId(String gatewayRefernceId);

}
