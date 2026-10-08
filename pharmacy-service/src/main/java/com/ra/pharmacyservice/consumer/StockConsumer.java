package com.ra.pharmacyservice.consumer;

import com.ra.pharmacyservice.entity.Medicine;
import com.ra.pharmacyservice.entity.Order;
import com.ra.pharmacyservice.event.OrderEvent;
import com.ra.pharmacyservice.repository.MedicineRepository;
import com.ra.pharmacyservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockConsumer {

    private final MedicineRepository medicineRepository;
    private final OrderRepository orderRepository;

    public StockConsumer(MedicineRepository medicineRepository, OrderRepository orderRepository) {
        this.medicineRepository = medicineRepository;
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "medicine-stock-events", groupId = "inventory-group")
    @Transactional
    public void handleOrderEvent(OrderEvent event) {
        System.out.println("=== Consumer nhan duoc su kien Order ===");

        Medicine medicine = medicineRepository.findById(event.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay thuoc voi ID: " + event.getMedicineId()));

        Order order = Order.builder()
                .medicineId(event.getMedicineId())
                .quantity(event.getQuantity())
                .priceSell(medicine.getPrice())
                .totalAmount(medicine.getPrice() * event.getQuantity())
                .timestamp(event.getTimestamp())
                .build();
        orderRepository.save(order);

        int updatedStock = medicine.getQuantity() - event.getQuantity();
        medicine.setQuantity(updatedStock);
        medicineRepository.save(medicine);

        System.out.println("-> Da luu don hang moi va cap nhat ton kho thuoc ID "
                + medicine.getId() + " con: " + updatedStock);
    }
}