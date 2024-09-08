package com.hello.jdbc.service.order;

// 체크 예외
public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
