package com.fintech.platform.upi.payments.api;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentDTOs {
	
	public record PaymentInitiationRequest(
				@NotNull UUID userId,
				@NotNull UUID sourceBankAccountId,
				@NotBlank String targetVPA,
				@Min(100) Long amountInPaise
			) {};
	
	public record PaymentInitiationResponse(
				UUID userId,
				String status,
				String message
			) {
		
	}
	
	public record WebHookRequest(
				@NotNull String gatewayReferenceId,
				@NotNull String finalStatus
			) {
		
	}
	
	public record transferPaymentInitiationRequest(
				@NotNull String userId, 
				@NotNull String sourceBankAccountId, 
				@NotNull String targetVPA, 
				@NotNull Long amountInPaise
			) {
		
	}

}
