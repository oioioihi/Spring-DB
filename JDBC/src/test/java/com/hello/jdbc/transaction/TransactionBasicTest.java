package com.hello.jdbc.transaction;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
public class TransactionBasicTest {


    @Autowired
    BasicService basicService; // Basic를 상속 받은 프록시 객체가 스프링 컨테이너에 빈으로 등록 된다. (다형성을 활용한 같은 타입의 Bean)

    @Test
    void proxyCheck() {
        log.info("aop class = {}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
        basicService.transaction();
        basicService.nonTransaction();
    }

    @TestConfiguration
    static class TransactionApplyBasicConfig {

        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        @Transactional // 클래스의 메서드레벨의 어노테이션이 가장 우선 순위가 높다.
        public void transaction() {
            log.info("call transaction");
            boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("transaction active = {}", isTransactionActive);
        }

        public void nonTransaction() {
            log.info("call no transaction");
            boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("transaction active = {}", isTransactionActive);
        }
    }
}
