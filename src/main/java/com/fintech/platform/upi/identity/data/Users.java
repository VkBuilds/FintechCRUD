package com.fintech.platform.upi.identity.data;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, unique = true, length = 15)
	private String phoneNumber;
	
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Instant createdAt;
	
	public enum UserStatus {
		PENDING_VERIFICATION, ACTIVE, BLOCKED 
	}
	
	protected Users() {
		
	}

	public Users(String phoneNumber) {
		super();
		this.phoneNumber = phoneNumber;
		this.userStatus = UserStatus.PENDING_VERIFICATION;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
