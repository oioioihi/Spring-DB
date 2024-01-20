package com.hello.jdbc.connection;

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

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connectioin = {}, class = {}",con1,con1.getClass());
        log.info("connectioin = {}, class = {}",con2,con2.getClass());
    }
}
