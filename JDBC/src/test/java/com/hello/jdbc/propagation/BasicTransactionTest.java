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

    @Test
    void double_commit() {

        log.info(" 트랜잭션 1 시작 ");
        TransactionStatus transaction1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 1 커밋 시작");
        transactionManager.commit(transaction1);

        log.info(" 트랜잭션 2 시작 ");
        TransactionStatus transaction2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 2 커밋 시작");
        transactionManager.commit(transaction2);


        /**
         - Hikari Connection Pool에서 커넥션을 획득하면 실제 커넥션을 그대로 반환하는 것이 아니라, 내부 관리를 위해 히카리 프록시 커넥션이라는
         객체를 생성해서 반환한다.
         - 물론 내부에는 실제 커넥션이 포함되어 있다.
         - 이 객체의 주소를 확인하면 커넥션 풀에서 획득한 커넥션을 구분할 수 있다.
         */
    }

    @Test
    void double_commit_rollback() {

        log.info(" 트랜잭션 1 시작 ");
        TransactionStatus transaction1 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 1 커밋 시작");
        transactionManager.commit(transaction1);

        log.info(" 트랜잭션 2 시작 ");
        TransactionStatus transaction2 = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션 2 롤백");
        transactionManager.rollback(transaction2);

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
