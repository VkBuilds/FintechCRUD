package com.fintech.platform.upi.identity.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceBindingRepository extends JpaRepository<DeviceBinding, UUID>{
	
	Optional<DeviceBinding> findByUserAndDeviceId(Users user,String deviceId);

}
