package com.fintech.platform.upi.identity.data;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;


@Repository
public interface BankAccountRepository extends JpaRepository<BankAccounts, UUID>{
	/*
	 * @Lock(LockModeType.PESSIMISTIC_WRITE)
	 * 
	 * @Query("SELECT b FROM BankAccounts b WHERE b.id = :id")
	 * Optional<BankAccounts> findByIdLocked(@Param("id") java.util.UUID id);
	 */

	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankAccounts b WHERE b.vpa = :vpa")
    Optional<BankAccounts> findByVpa(@Param("vpa") String vpa);

}
