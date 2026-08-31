package com.example.payment;

public enum PaymentEvent {
    AUTHORIZE,
    AUTHORIZE_SUCCESS,
    AUTHORIZE_FAIL,
    CAPTURE,
    CAPTURE_SUCCESS,
    CAPTURE_FAIL,
    REFUND
}
