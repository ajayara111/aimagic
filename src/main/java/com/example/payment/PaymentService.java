package com.example.payment;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final Map<String, StateMachine<PaymentState, PaymentEvent>> stateMachineRegistry = new ConcurrentHashMap<>();

    public PaymentService(StateMachineFactory<PaymentState, PaymentEvent> factory) {
        this.factory = factory;
    }

    public String createPayment() {
        String paymentId = UUID.randomUUID().toString();
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(paymentId);
        
        sm.startReactively().subscribe();
        stateMachineRegistry.put(paymentId, sm);
        return paymentId;
    }

    public PaymentState sendEvent(String paymentId, PaymentEvent event) {
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineRegistry.get(paymentId);
        if (sm == null) {
            throw new IllegalArgumentException("Payment ID not found");
        }

        Message<PaymentEvent> message = MessageBuilder.withPayload(event)
                .setHeader("paymentId", paymentId)
                .build();

        sm.sendEvent(Mono.just(message)).subscribe();
        return sm.getState().getId();
    }

    public PaymentState getStatus(String paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineRegistry.get(paymentId);
        if (sm == null) {
            throw new IllegalArgumentException("Payment ID not found");
        }
        return sm.getState().getId();
    }
}
