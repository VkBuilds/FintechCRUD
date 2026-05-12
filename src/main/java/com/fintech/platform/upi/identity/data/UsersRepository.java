package com.fintech.platform.upi.identity.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID>{
	
	Optional<Users> findByPhoneNumber(String phoneNumber);

}
