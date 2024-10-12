package com.stream.error.handle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class KafkaErrorHandlingWithRetryApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaErrorHandlingWithRetryApplication.class, args);
	}

}
