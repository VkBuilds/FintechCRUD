package com.fintech.platform.upi.identity.core;


import java.util.UUID;

 
import org.springframework.stereotype.Service;

import com.fintech.platform.upi.identity.api.IdentityDTOs.AddBankRequest;
import com.fintech.platform.upi.identity.api.IdentityDTOs.RegistrationRequest;
import com.fintech.platform.upi.identity.api.IdentityDTOs.RegistrationResponse;
import com.fintech.platform.upi.identity.api.IdentityDTOs.VerifyRegistration;
import com.fintech.platform.upi.identity.data.BankAccountRepository;
import com.fintech.platform.upi.identity.data.BankAccounts;
import com.fintech.platform.upi.identity.data.DeviceBinding;
import com.fintech.platform.upi.identity.data.DeviceBinding.BindingStatus;
import com.fintech.platform.upi.identity.data.DeviceBindingRepository;
import com.fintech.platform.upi.identity.data.Users;
import com.fintech.platform.upi.identity.data.Users.UserStatus;
import com.fintech.platform.upi.identity.data.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	private UsersRepository usersRepository;
	private DeviceBindingRepository deviceBindingRepository;
	private BankAccountRepository bankAccountRepository;
	
	public UserService(UsersRepository usersRepository, DeviceBindingRepository deviceBindingRepository, BankAccountRepository bankAccountRepository) {
		super();
		this.bankAccountRepository = bankAccountRepository;
		this.usersRepository = usersRepository;
		this.deviceBindingRepository = deviceBindingRepository;
	}
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserService.class);

	@Transactional
	public RegistrationResponse UserRegistration(RegistrationRequest request) {
		
		log.info("Initiating registration for Phone: {}", request.phoneNumber());
		
		Users user = usersRepository.findByPhoneNumber(request.phoneNumber())
				.orElseGet(() -> {
					log.info("New User detected.....Creating Account");
					return usersRepository.save(new Users(request.phoneNumber()));
				});
		
		DeviceBinding deviceBinding = deviceBindingRepository.findByUserAndDeviceId(user, request.deviceId())
				.orElseGet(() -> {
					log.info("New Device detected....Initiating binding");
					return deviceBindingRepository.save(new DeviceBinding(request.deviceId(), user));
				});
		
		deviceBinding.setBindingStatus(DeviceBinding.BindingStatus.INITIATED);
		deviceBindingRepository.save(deviceBinding);
		
		return new RegistrationResponse(
				user.getId().toString(),
                user.getUserStatus().name(),
                "Registration initiated. Awaiting device verification (OTP/Silent SMS)."
				);
	}
	
	@Transactional
	public String AddTestAccounts (String userId, AddBankRequest request) {
		
		Users user = usersRepository.findById(java.util.UUID.fromString(userId))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		if(user.getUserStatus() != Users.UserStatus.ACTIVE) {
			throw new IllegalArgumentException("User must be active to add bank account");
		}
		
		BankAccounts newAccount = new BankAccounts(
				user,
				request.bankName(),
				request.maskedAccountNumber(),
				request.ifscCode(),
				true
				);
		
		String generatedVpa = "user-" + java.util.UUID.randomUUID().toString().substring(0, 5) + "@hdfc";
	    newAccount.setVpa(generatedVpa);
	    
		newAccount = bankAccountRepository.save(newAccount);
		
		return newAccount.getId().toString(); 
	}
	
	@Transactional
	public String Verification(VerifyRegistration request) {
		Users user = usersRepository.findById(java.util.UUID.fromString(request.userId()))
				.orElseThrow(()-> new IllegalArgumentException("User not found"));
		
		DeviceBinding deviceBinding = deviceBindingRepository.findByUserAndDeviceId(user, request.deviceId())
				.orElseThrow(()-> new IllegalArgumentException("Device Binding not found"));
		
		if(!"123456".equals(request.otp())) {
			throw new IllegalArgumentException("Invalid OTP");
		}
		
		user.setUserStatus(UserStatus.ACTIVE);
		deviceBinding.setBindingStatus(BindingStatus.VERIFIED);
		
		return "Device verified successfully. Account is now ACTIVE.";
	}
	
	@Transactional
	public boolean veridyBankAccountForUser(String userId, String bankAccountId) {
		
		Users user = usersRepository.findById(java.util.UUID.fromString(userId))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		if(user.getUserStatus() != Users  .UserStatus.ACTIVE) {
			throw new IllegalArgumentException("User Account not Active");
		}
		
		BankAccounts bankAccount = bankAccountRepository.findById(java.util.UUID.fromString(bankAccountId))
				.orElseThrow(()-> new IllegalArgumentException("Bank Account not found"));		
		
		if(!bankAccount.getUser().getId().equals(user.getId())){
			throw new SecurityException("CRITICAL: Bank account does not belong to the requesting user!");
		}
		
		if(!bankAccount.isUpiPinSet()) {
			throw new IllegalStateException("UPI pin is not set for this account");
		}
		
		return true;
	}

	@Transactional
	public void deductBalance(String userId, String sourceBankAccountId, Long amountInPaise) {
		
		BankAccounts account = bankAccountRepository.findById(UUID.fromString(sourceBankAccountId))
				.orElseThrow(()-> new IllegalArgumentException("Bank Account not found"));
		
		if(!account.getUser().getId().toString().equals(userId)) {
			throw new IllegalArgumentException("Unauthorized attempt!!!");
		}
		
		if(account.getBalanceInPaise() <= amountInPaise) {
			throw new IllegalArgumentException("Insufficient Funds");
		}
		
		account.setBalanceInPaise(account.getBalanceInPaise() - amountInPaise);
		bankAccountRepository.save(account);
		
	}
	
	@Transactional
	public Long getBalance(String userId, String bankAccountId) {
		
		BankAccounts account = bankAccountRepository.findById(UUID.fromString(bankAccountId))
				.orElseThrow(()-> new IllegalArgumentException("Bank Account not found"));
		
		if(!account.getUser().getId().toString().equals(userId)) {
			throw new SecurityException("Unauthorized attempt!!!");
		}
		
		return account.getBalanceInPaise();
		
	}
	
	@org.springframework.transaction.annotation.Transactional
	public void transferFund(String userID, String sourceBankAccountId, String targetVPA, Long amountInPaise) {
		
		BankAccounts senderAccount  = bankAccountRepository.findById(UUID.fromString(sourceBankAccountId))
				.orElseThrow(()-> new IllegalArgumentException("Account Not Found"));
		
		if(!senderAccount.getUser().getId().toString().equals(userID)) {
			throw new SecurityException("Unauthorized Attempt!!!");
		}
		
		if(senderAccount.getBalanceInPaise()<= amountInPaise) {
			throw new IllegalArgumentException("Insuffiecient Funds");
		}
		
		BankAccounts receiverAccount = bankAccountRepository.findByVpa(targetVPA)
				.orElseThrow(()-> new IllegalArgumentException("Target VPA doesnot exist"));
		
		senderAccount.setBalanceInPaise(senderAccount.getBalanceInPaise() - amountInPaise);
		
		receiverAccount.setBalanceInPaise(receiverAccount.getBalanceInPaise() + amountInPaise);
		
		bankAccountRepository.save(senderAccount);
		bankAccountRepository.save(receiverAccount);
	}

}
