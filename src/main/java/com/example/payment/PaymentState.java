package com.example.payment;

public enum PaymentState {
    NEW,
    AUTHORIZING,
    AUTHORIZED,
    AUTHORIZE_FAILED,
    CAPTURING,
    CAPTURED,
    CAPTURE_FAILED,
    REFUNDED
}
