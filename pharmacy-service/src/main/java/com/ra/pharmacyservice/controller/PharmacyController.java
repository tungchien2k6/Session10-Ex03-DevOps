package com.ra.pharmacyservice.controller;

import com.ra.pharmacyservice.event.OrderEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public PharmacyController(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellMedicine(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity) {

        OrderEvent event = OrderEvent.builder()
                .medicineId(medicineId)
                .quantity(quantity)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("medicine-stock-events", medicineId.toString(), event);

        return ResponseEntity.ok("Thanh toán thành công! Sự kiện đã được gửi tới Kafka.");
    }
}