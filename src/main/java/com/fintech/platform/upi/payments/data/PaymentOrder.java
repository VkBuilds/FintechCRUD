package com.fintech.platform.upi.payments.data;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "payments-order")
public class PaymentOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private UUID sourceBankAccountId;
	
	@Column(nullable = false)
	private UUID userId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus orderstatus;
	
	@Column(nullable = false)
	private Long amountInPaise;
	
	@Column(nullable = false,unique = true,updatable = false)
	private String idempotencyKey;
	
	@Column(nullable = false)
	private String targetVPA;
	
	@Version
	private Integer version;
	
	@CreationTimestamp
	@Column(nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
	protected PaymentOrder() {
		// TODO Auto-generated constructor stub
	}
	
	public PaymentOrder(UUID userId, UUID sourceBankAccountId, String targetVPA, Long amountInPaise, String idempotencyKey) {
        this.userId = userId;
        this.sourceBankAccountId = sourceBankAccountId;
        this.targetVPA = targetVPA;
        this.amountInPaise = amountInPaise;
        this.idempotencyKey = idempotencyKey;
        this.orderstatus = OrderStatus.PENDING;
    }
	
	
	public String getTargetVPA() {
		return targetVPA;
	}

	public void setTargetVPA(String targetVPA) {
		this.targetVPA = targetVPA;
	}

	public UUID getSourceBankAccountId() {
		return sourceBankAccountId;
	}

	public void setSourceBankAccountId(UUID sourceBankAccountId) {
		this.sourceBankAccountId = sourceBankAccountId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public OrderStatus getOrderstatus() {
		return orderstatus;
	}

	public void setOrderstatus(OrderStatus orderstatus) {
		this.orderstatus = orderstatus;
	}

	public Long getAmountInPaise() {
		return amountInPaise;
	}

	public void setAmountInPaise(Long amountInPaise) {
		this.amountInPaise = amountInPaise;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}



	public enum OrderStatus {
		PENDING, SUCCESS, FAILED
	}
}
