package com.fintech.platform.upi.payments.data;

import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "transaction_attempts")
public class TransactionAttempt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "order_id",nullable = false)
	private PaymentOrder paymentorder;
	
	@Column(nullable = false)
	private String gatewayName;
	
	private String gatewayReferenceId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttemptStatus attemptStatus;
	
	@JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> rawGatewayResponse;
	
	protected TransactionAttempt() {
		
	}
	
	public TransactionAttempt(PaymentOrder paymentOrder, String gatewayName) {
        this.paymentorder = paymentOrder;
        this.gatewayName = gatewayName;
        this.attemptStatus = AttemptStatus.INITIATED;
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public PaymentOrder getPaymentorder() {
		return paymentorder;
	}

	public void setPaymentorder(PaymentOrder paymentorder) {
		this.paymentorder = paymentorder;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getGatewayReferenceId() {
		return gatewayReferenceId;
	}

	public void setGatewayReferenceId(String gatewayReferenceId) {
		this.gatewayReferenceId = gatewayReferenceId;
	}

	public AttemptStatus getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(AttemptStatus attemptStatus) {
		this.attemptStatus = attemptStatus;
	}

	public Map<String, Object> getRawGatewayResponse() {
		return rawGatewayResponse;
	}

	public void setRawGatewayResponse(Map<String, Object> rawGatewayResponse) {
		this.rawGatewayResponse = rawGatewayResponse;
	}

	public enum AttemptStatus{
		COMPLETED, INITIATED, FAILED
	}

}
