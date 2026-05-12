package com.fintech.platform.upi.identity.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.platform.upi.identity.api.IdentityDTOs.AddBankRequest;
import com.fintech.platform.upi.identity.api.IdentityDTOs.RegistrationRequest;
import com.fintech.platform.upi.identity.api.IdentityDTOs.RegistrationResponse;
import com.fintech.platform.upi.identity.api.IdentityDTOs.VerifyRegistration;
import com.fintech.platform.upi.identity.core.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {
	
	private UserService userService;

	public IdentityController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<RegistrationResponse> registerDevice(@Valid @RequestBody RegistrationRequest request){
		
		RegistrationResponse response = userService.UserRegistration(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/{userId}/banks")
	public ResponseEntity<String> addManualBanks(@PathVariable String userId, @Valid @RequestBody AddBankRequest request){
		
		String bankAccountId = userService.AddTestAccounts(userId, request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Bank Account added successfully with Account Id " + bankAccountId);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyRegistration(@Valid @RequestBody VerifyRegistration request){
		
		String newResponse = userService.Verification(request);
		
		return ResponseEntity.ok(newResponse);
	}
	
	@GetMapping("/accountbalance")
	public ResponseEntity<String> getBalance(@RequestParam String bankAccountId,
            @RequestParam String userId){
		
		Long balanceinPaise = userService.getBalance(userId, bankAccountId);
		
		double balanceInRupees = balanceinPaise/100.0;
		return ResponseEntity.ok("Current Balance: ₹" + balanceInRupees);
		
		
	}

}
