package com.fintech.platform.upi;

import org.springframework.boot.SpringApplication;

public class TestUpiApplication {

	public static void main(String[] args) {
		SpringApplication.from(UpiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
