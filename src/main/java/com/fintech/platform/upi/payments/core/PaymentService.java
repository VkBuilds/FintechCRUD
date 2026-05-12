package com.fintech.platform.upi.payments.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.fintech.platform.upi.identity.core.UserService;
import com.fintech.platform.upi.identity.data.BankAccountRepository;
import com.fintech.platform.upi.identity.data.BankAccounts;
import com.fintech.platform.upi.notification.PaymentEventPublisher;
import com.fintech.platform.upi.payments.api.PaymentDTOs.PaymentInitiationRequest;
import com.fintech.platform.upi.payments.api.PaymentDTOs.PaymentInitiationResponse;
import com.fintech.platform.upi.payments.api.PaymentDTOs.WebHookRequest;
import com.fintech.platform.upi.payments.data.PaymentOrder;
import com.fintech.platform.upi.payments.data.PaymentOrderRepository;
import com.fintech.platform.upi.payments.data.TransactionAttempt;
import com.fintech.platform.upi.payments.data.TransactionAttemptRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class PaymentService {

	private final PaymentOrderRepository orderRepository;
	private final TransactionAttemptRepository attemptRepository;
	private final BankAccountRepository bankAccountRepository;
	private final UserService userService;
	private final PaymentEventPublisher eventPublisher;

	public PaymentService(PaymentOrderRepository orderRepository, TransactionAttemptRepository attemptRepository,
			BankAccountRepository bankAccountRepository, UserService userService,
			PaymentEventPublisher eventPublisher) {
		super();
		this.orderRepository = orderRepository;
		this.attemptRepository = attemptRepository;
		this.bankAccountRepository = bankAccountRepository;
		this.userService = userService;
		this.eventPublisher = eventPublisher;
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentService.class);

	@Transactional
	public PaymentInitiationResponse processPayment(String idempotencyKey, PaymentInitiationRequest request) {

		log.info("Initiating payment of {} paise for user {}", request.amountInPaise(), request.userId());

		// FIX: Call the correct method, passing the strings from the request!
		userService.veridyBankAccountForUser(request.userId().toString(), request.sourceBankAccountId().toString());

		Optional<PaymentOrder> existingOrder = orderRepository.findByIdempotencyKey(idempotencyKey);

		if (existingOrder.isPresent()) {

			PaymentOrder order = existingOrder.get();
			return new PaymentInitiationResponse(order.getUserId(), order.getOrderstatus().name(),
					"Returned existing order due to idempotency key match.");
		}

		PaymentOrder newOrder = new PaymentOrder(request.userId(), request.sourceBankAccountId(), request.targetVPA(),
				request.amountInPaise(), idempotencyKey);

		newOrder = orderRepository.save(newOrder);

		TransactionAttempt attempt = new TransactionAttempt(newOrder, "RAZORPAY");

		attempt.setGatewayReferenceId("sim_" + java.util.UUID.randomUUID().toString().substring(0, 8));

		attemptRepository.save(attempt);

		return new PaymentInitiationResponse(newOrder.getId(), newOrder.getOrderstatus().name(),
				"Payment initiated success fully.");
	}

	@Transactional
	public String handleWebHook(@Valid @RequestBody WebHookRequest request) {

		TransactionAttempt attempt = attemptRepository.findByGatewayReferenceId(request.gatewayReferenceId())
				.orElseThrow(() -> new IllegalArgumentException("Unknow Transaction Reference"));

		if (attempt.getAttemptStatus() == TransactionAttempt.AttemptStatus.COMPLETED
				|| attempt.getAttemptStatus() == TransactionAttempt.AttemptStatus.FAILED) {
			return "WebHook already passed";
		}

		PaymentOrder order = attempt.getPaymentorder();

		if ("SUCCESS".equalsIgnoreCase(request.finalStatus())) {

			Optional<BankAccounts> localReceiver = bankAccountRepository.findByVpa(order.getTargetVPA());

			try {

				if (localReceiver.isPresent()) {
					userService.transferFund(order.getUserId().toString(), order.getSourceBankAccountId().toString(),
							order.getTargetVPA(), order.getAmountInPaise());

					attempt.setAttemptStatus(TransactionAttempt.AttemptStatus.COMPLETED);
					order.setOrderstatus(PaymentOrder.OrderStatus.SUCCESS);
				} else {
					String masterSettlementVPA = "settlement@myapp";
					userService.transferFund(order.getUserId().toString(), order.getSourceBankAccountId().toString(),
							masterSettlementVPA, order.getAmountInPaise());

					attempt.setAttemptStatus(TransactionAttempt.AttemptStatus.COMPLETED);
					order.setOrderstatus(PaymentOrder.OrderStatus.SUCCESS);
				}

			} catch (Exception e) {
				attempt.setAttemptStatus(TransactionAttempt.AttemptStatus.FAILED);
				order.setOrderstatus(PaymentOrder.OrderStatus.FAILED);
			}
		} else {
			attempt.setAttemptStatus(TransactionAttempt.AttemptStatus.FAILED);
			order.setOrderstatus(PaymentOrder.OrderStatus.FAILED);
		}
		orderRepository.save(order);
		attemptRepository.save(attempt);

		
		  if(order.getOrderstatus() == PaymentOrder.OrderStatus.SUCCESS) { try {
		  eventPublisher.publishPaymentSuccess(order.getId().toString(),
		  "user-"+order.getTargetVPA()+".com"); } catch (Exception e) {
		  System.out.println("⚠️ WARNING: Money transferred, but Kafka event failed: "
		  + e.getMessage()); } }
		 

		return "Webhook processed successfully. Order updated to " + request.finalStatus();

	}

	public List<PaymentOrder> getTxnHistory(String userId) {
		// TODO Auto-generated method stub
		List<PaymentOrder> history = orderRepository.findByUserIdOrderByCreatedAtDesc(UUID.fromString(userId));
		return history;
	}
}
