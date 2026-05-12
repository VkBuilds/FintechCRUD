package com.fintech.platform.upi.identity.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IdentityDTOs {
	
	public record RegistrationRequest(
				@NotBlank
				@Pattern(regexp = "^\\+91[6-9]\\d{9}$", message = "Must be a valid Indian mobile number")
				String phoneNumber,
				@NotBlank String deviceId
			) {}

    public record RegistrationResponse(
            String userId,
            String status,
            String message
    ) {}
    
    public record AddBankRequest(
    			@NotBlank String bankName,
    			@NotBlank String maskedAccountNumber,
    			@NotBlank String ifscCode
    		) {}
    
    public record VerifyRegistration(
    			@NotNull String userId,
    			@NotNull String deviceId,
    			@NotNull String otp
    		) {}

}
