package com.fintech.platform.upi.notification;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {
	
	private final KafkaTemplate<String, String> kafkaTemplate;

	public PaymentEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void publishPaymentSuccess(String paymentOrderId, String emailId) {
		
		String ticket = String.format("{\"orderId\": \"%s\", \"email\": \"%s\"}\")", paymentOrderId, emailId); 
		
		kafkaTemplate.send("Payment Succes", ticket);
		
		System.out.println("🎟️ Ticket published to Kafka: " + ticket);
	}

}
