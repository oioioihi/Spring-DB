package com.hello.jdbc.transaction;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class BeanInitTransactionTest {

    @Autowired
    Hello hello;

    @Test
    void go() {
        // @PostConstruct는 스프링이 초기화 되는 시점에 호출한다.
        // 왜냐하면 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP가 적용되기 때문이다.
        // 따라서 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 없다.
    }

    @TestConfiguration
    static class Config {

        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct transaction active = {}", isTransactionActive);
        }

        @EventListener(ApplicationReadyEvent.class) // 스프링 컨테이너가 완전히 준비가 된 상태 -> AOP, 트랜잭션이 모두 주입이 된 상태
        @Transactional
        public void initV2() {
            boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent transaction active = {}", isTransactionActive);
        }
    }
}
