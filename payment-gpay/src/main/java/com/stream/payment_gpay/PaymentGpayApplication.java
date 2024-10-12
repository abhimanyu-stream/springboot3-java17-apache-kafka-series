package com.stream.payment_gpay;

import com.stream.payment_gpay.model.Account;
import com.stream.payment_gpay.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Random;

@SpringBootApplication
@EnableTransactionManagement
public class PaymentGpayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentGpayApplication.class, args);
	}





}

/***
 * 1. Microservices Architecture:
 * •	Spring Boot: Use Spring Boot to develop microservices for various functionalities in the payment processing system, such as payment initiation, fraud detection, payment authorization, and notification services.
 * •	Scalability: Microservices allow you to scale individual components independently based on the load.
 * 2. Apache Kafka for Messaging:
 * •	High Throughput Messaging: Kafka is ideal for high-throughput messaging and event-driven architectures. It ensures that the microservices can communicate efficiently with each other.
 * •	Topic Design: Design Kafka topics based on the business requirements, such as payment-requests, payment-authorizations, payment-status, etc. Each service can publish or subscribe to relevant topics.
 * •	Partitions: To achieve high throughput, Kafka topics can be partitioned. Each partition can be processed independently, allowing for parallel processing.
 * •	Consumer Groups: Use Kafka consumer groups to distribute the processing load across multiple instances of the microservices.
 * 3. Workflow Example:
 * •	Payment Request: A payment request is received by the PaymentService microservice. This service validates the request and publishes an event to the payment-requests Kafka topic.
 * •	Fraud Detection: The FraudDetectionService consumes messages from the payment-requests topic. It processes the message, performs fraud checks, and publishes the result to the payment-fraud-check topic.
 * •	Authorization: The AuthorizationService consumes messages from the payment-fraud-check topic. If the payment passes fraud checks, it authorizes the payment and publishes the result to the payment-authorizations topic.
 * •	Payment Processing By delegate request to Payment Gatway: It will listen the payment-authorizations topic. make a kafak topic named payment-processed-topic
 * •	Notification: The NotificationService listens to the payment-processed-topic and sends a confirmation or failure message to the user.
 * 4. Data Consistency:
 * •	Transaction Management: Use Kafka's support for transactional messaging to ensure that each step in the payment process is either fully completed or rolled back in case of failure.
 * •	Outbox Pattern: Implement the outbox pattern to ensure data consistency between the microservices and Kafka. This involves writing events to a local database first and then publishing them to Kafka, ensuring that if a failure occurs, the event can be re-published.
 * 5. Error Handling & Monitoring:
 * •	Retry Mechanism: Implement retry logic in case of temporary failures when processing Kafka messages. Kafka consumer configurations can be tuned for this.
 * •	Dead Letter Queue (DLQ): Use Kafka DLQs to handle messages that fail processing multiple times.
 * •	Monitoring: Use tools like Prometheus, Grafana, and Kafka’s native monitoring tools to monitor the health of the microservices and Kafka clusters.
 * 6. Scaling Considerations:
 * •	Horizontal Scaling: Deploy multiple instances of each microservice and scale them based on the load. Kafka's partitioning allows you to process messages concurrently across different instances.
 * •	Load Balancing: Use load balancers to distribute requests across multiple instances of a microservice.
 * 7. Security:
 * •	Encryption & Authentication: Ensure that all communications between microservices and Kafka are encrypted (e.g., using SSL/TLS) and that authentication mechanisms (e.g., OAuth2, JWT) are in place to secure the endpoints.
 * •	Authorization: Implement fine-grained access controls using role-based access control (RBAC) or similar mechanisms.
 * */

/***
 * Flow of Data:
 * Client sends a payment request to the Payment Request Service.
 * Payment Request Service publishes the request to the payment-requests topic.
 * Payment Authorization Service consumes from the payment-requests topic, authorizes the payment, and publishes the result to the payment-authorizations topic.
 * Fraud Detection Service consumes from the payment-requests or payment-authorizations topics, detects fraud if applicable, and may publish results to the fraud-detection topic.
 * Payment Status Service consumes from the payment-authorizations topic, updates the payment status, and publishes the update to the payment-status topic.
 *
 * */
