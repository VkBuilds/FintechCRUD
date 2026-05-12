package com.fintech.platform.upi.identity.data;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank-accounts")
public class BankAccounts {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Column(nullable = false)
	private String vpa;
	
	@Column(nullable = false)
	private String bankName;
	
	@Column(nullable = false)
	private String maskedAccountNumber;
	
	@Column(nullable = false)
	private String ifscCode;

    @Column(nullable = false)
    private Long balanceInPaise = 0L; 
	
	@Column(nullable = false)
	private boolean upiPinSet;
	
	@CreationTimestamp
	private Instant linkedAt;
	
	protected BankAccounts() {
		
	}

	public BankAccounts(Users user, String bankName, String maskedAccountNumber, String ifscCode, boolean upiPinSet) {
		super();
		this.user = user;
		this.bankName = bankName;
		this.maskedAccountNumber = maskedAccountNumber;
		this.ifscCode = ifscCode;
		this.upiPinSet = upiPinSet;
	}

    public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public Long getBalanceInPaise() {
        return balanceInPaise;
    }

    public void setBalanceInPaise(Long balanceInPaise) {
        this.balanceInPaise = balanceInPaise;
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getMaskedAccountNumber() {
		return maskedAccountNumber;
	}

	public void setMaskedAccountNumber(String maskedAccountNumber) {
		this.maskedAccountNumber = maskedAccountNumber;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public boolean isUpiPinSet() {
		return upiPinSet;
	}

	public void setUpiPinSet(boolean upiPinSet) {
		this.upiPinSet = upiPinSet;
	}

	public Instant getLinkedAt() {
		return linkedAt;
	}

	public void setLinkedAt(Instant linkedAt) {
		this.linkedAt = linkedAt;
	}
	
	

}
