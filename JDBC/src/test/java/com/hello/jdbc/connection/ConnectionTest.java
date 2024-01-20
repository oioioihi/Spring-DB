package com.hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException{

        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        // DriverManager는 커넥션을 획득할 때 마다 URL, USERNAME, PASSWORD 같은 파라미터를 계속 전달해야 한다.
        log.info("connectioin = {}, class = {}",con1,con1.getClass());
        log.info("connectioin = {}, class = {}",con2,con2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {

        //DriverManagerDataSource는 항상 새로운 커넥션을 획득한다.
        // DataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD); 아래와 같음
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        // DB 접속 정보를 한번만 사용해서 DataSource 객체를 만들기 때문에, 커넥션을 사용하는 로직에선 URL, USERNAME, PASSWORD 같은 정보를 신경쓸 필요없다.
        useDataSource(driverManagerDataSource);
    }

    @Test
    void dataSourceConnectionaPool() throws SQLException, InterruptedException{

        // HikariCP 를 이용한 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("my pool");

        useDataSource(dataSource);

        /**
         * 커넥션 풀에 커넥션을 채우는 것은 상대적으로 오래 걸리는 일이다. (TCP/IP 연결 소요)
         * 애플리케이션을 실행할 때 커넥션 풀을 채울 때 까지 마냥 대기하고 잇다면 애플리케이션 실행 시간이 늦어진다.
         * 따라서 이렇게 별도의 쓰레드를 사용해서 커넥션 풀을 채워야 애플리케이션 실행 시간에 영향을 주지 않는다.
        */
        Thread.sleep(1000);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connectioin = {}, class = {}",con1,con1.getClass());
        log.info("connectioin = {}, class = {}",con2,con2.getClass());
    }
}
