package com.hello.jdbc.transaction;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    CallService callService;

    @Test
    void 트랜잭션_프록시객체_테스트() {
        log.info("callService Class = {}", callService.getClass());
        //InternalCallV1Test$CallService$$SpringCGLIB$$0

    }

    @Test
    void 트랜잭션_내부_호출_테스트() {
        log.info("외부 호출 ==========================");
        callService.internal();

        log.info("내부 호출 ==========================");
        callService.external();

    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    static class CallService {

        /**
         * 트랜잭션 호출 흐름
         * 1. 클라이언트인 테스트코드에서 'callService.external()'을 호출한다. 여기서 'callService()'는 트랜잭션 프록시이다.
         * 2. 'callService()'의 트랜잭션 프록시가 호출된다.
         * 3. 'external()'메서드에는 '@Transactional'이 없으므로 프록시 객체가 트랜잭션 적용을 하지 않는다.
         * 4. 트랜잭션을 적용하지 않은 상태로 타켓 인스턴스의 'exeternal()'을 호출한다.
         * 5. 'external()'은 내부에서 this.internal()을 호출한다.
         * <p>
         * 즉 this(자기 자신의 인스턴스를 기리킴)를 호출함으로써 프록시 객체를 거치지 않게 된다.
         * 따라서 트랜잭션이 적용되지 않는다.
         */
        public void external() {
            log.info("call external");
            printTransactionInfo();
            internal();
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTransactionInfo();
        }

        private void printTransactionInfo() {
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("transaction active = {}", transactionActive);
            boolean currentTransactionReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("transaction readOnly = {}", currentTransactionReadOnly);
        }
    }
}
