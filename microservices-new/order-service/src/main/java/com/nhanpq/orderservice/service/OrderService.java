package com.nhanpq.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.nhanpq.orderservice.dto.InventoryResponse;
import com.nhanpq.orderservice.dto.OrderLineItemsDto;
import com.nhanpq.orderservice.dto.OrderRequest;
import com.nhanpq.orderservice.entity.Order;
import com.nhanpq.orderservice.entity.OrderLineItems;
import com.nhanpq.orderservice.event.OrderPlacedEvent;
import com.nhanpq.orderservice.repository.OrderRepository;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDto().stream().map(this::mapToEntity)
                .toList();
        order.setOrderLineItems(orderLineItems);

        List<String> listSkuCode = order.getOrderLineItems().stream().map(OrderLineItems::getSkuCode).toList();

        // call inventory service to check if the quantity is valid
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        inventoryServiceObservation = inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        return inventoryServiceObservation.observe(() -> {

            InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            t -> t.queryParam("skuCode", listSkuCode).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            Boolean allProductIsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::getIsInStock);

            if (allProductIsInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Orders placed successfully!";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later!");
            }
        });
                    
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .skuCode(orderLineItemsDto.getSkuCode())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }

}
