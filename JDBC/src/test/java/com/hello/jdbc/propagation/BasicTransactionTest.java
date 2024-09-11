package com.hello.jdbc.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTransactionTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    void commit() {

        log.info(" 트랜잭션 시작 ");
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        transactionManager.commit(transaction);
        log.info("트랜잭션 커밋 완료");

        /**
         * :  트랜잭션 시작
         * : Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
         * : Acquired Connection [HikariProxyConnection@820074024 wrapping conn0: url=jdbc:h2:mem:74faf340-6b07-4d1f-9b5b-a81d10fba4e8 user=SA] for JDBC transaction
         * : Switching JDBC Connection [HikariProxyConnection@820074024 wrapping conn0: url=jdbc:h2:mem:74faf340-6b07-4d1f-9b5b-a81d10fba4e8 user=SA] to manual commit
         * : 트랜잭션 커밋 시작
         * : Initiating transaction commit
         * : Committing JDBC transaction on Connection [HikariProxyConnection@820074024 wrapping conn0: url=jdbc:h2:mem:74faf340-6b07-4d1f-9b5b-a81d10fba4e8 user=SA]
         * : Releasing JDBC Connection [HikariProxyConnection@820074024 wrapping conn0: url=jdbc:h2:mem:74faf340-6b07-4d1f-9b5b-a81d10fba4e8 user=SA] after transaction
         * : 트랜잭션 커밋 완료
         */
    }

    @TestConfiguration
    static class Config {

        // 스프링 부트가 자동으로 'PlatformTransactionManager'를 등록해주지만 직접 bean으로 등록하면 해당 빈이 사용된다.
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }
}
