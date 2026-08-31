package com.example.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment() {
        String id = paymentService.createPayment();
        PaymentState status = paymentService.getStatus(id);
        return ResponseEntity.ok(Map.of("id", id, "status", status.name()));
    }

    @PostMapping("/{id}/event")
    public ResponseEntity<Map<String, String>> triggerEvent(@PathVariable String id, @RequestParam PaymentEvent event) {
        try {
            PaymentState newStatus = paymentService.sendEvent(id, event);
            return ResponseEntity.ok(Map.of("id", id, "status", newStatus.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable String id) {
        try {
            PaymentState status = paymentService.getStatus(id);
            return ResponseEntity.ok(Map.of("id", id, "status", status.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
