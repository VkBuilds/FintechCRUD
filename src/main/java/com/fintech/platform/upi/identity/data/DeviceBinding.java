package com.fintech.platform.upi.identity.data;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "device_binding")
public class DeviceBinding {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, unique = true)
    private String deviceId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Enumerated
	@Column(updatable = false)
	private BindingStatus bindingStatus;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Instant createdAt;
	
	protected DeviceBinding() {
		
	}

	public DeviceBinding(String deviceId, Users user) {
		super();
		this.deviceId = deviceId;
		this.user = user;
		this.bindingStatus = BindingStatus.INITIATED;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public BindingStatus getBindingStatus() {
		return bindingStatus;
	}

	public void setBindingStatus(BindingStatus bindingStatus) {
		this.bindingStatus = bindingStatus;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}



	public enum BindingStatus{
		REVOKED, INITIATED, VERIFIED
	}

}
