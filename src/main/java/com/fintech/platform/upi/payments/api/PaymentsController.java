package com.fintech.platform.upi.payments.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.platform.upi.payments.api.PaymentDTOs.PaymentInitiationRequest;
import com.fintech.platform.upi.payments.api.PaymentDTOs.PaymentInitiationResponse;
import com.fintech.platform.upi.payments.api.PaymentDTOs.WebHookRequest;
import com.fintech.platform.upi.payments.core.PaymentService;
import com.fintech.platform.upi.payments.data.PaymentOrder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {
	
	private final PaymentService paymentService;

    public PaymentsController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiationResponse> initiate(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentInitiationRequest request) {

        PaymentInitiationResponse response = paymentService.processPayment(idempotencyKey, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebHook(
    		@Valid @RequestBody WebHookRequest request){
    	
    	String response = paymentService.handleWebHook(request);
    	return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<PaymentOrder>> getTxnHistory(@RequestParam String userId){
    	
    	List<PaymentOrder> response = paymentService.getTxnHistory(userId);
    	return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
