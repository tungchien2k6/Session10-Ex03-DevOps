package com.ra.pharmacyservice.service.impl;

import com.ra.pharmacyservice.event.OrderEvent;
import com.ra.pharmacyservice.service.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private static final String TOPIC_NAME = "medicine-stock-events";

    public OrderServiceImpl(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String processOrder(OrderEvent orderEvent) {
        orderEvent.setOrderId(UUID.randomUUID().toString());
        orderEvent.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send(TOPIC_NAME, String.valueOf(orderEvent.getMedicineId()), orderEvent);

        return "Thanh toán thành công! Mã đơn hàng: " + orderEvent.getOrderId();
    }
}