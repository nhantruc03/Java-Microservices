package com.nhanpq.notficationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import com.nhanpq.notficationservice.event.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class NotficationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotficationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(OrderPlacedEvent orderPlacedEvent) {

		log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());

	}
}