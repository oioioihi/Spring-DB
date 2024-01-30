package com.hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class CheckedAppTest {

    @Test
    void checked() {

        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request()).isInstanceOf(Exception.class);
    }

    static class Controller {
        Service service = new Service();

        /**
         * - 체크 예외의 심각한 문제는 특정기술에 의해서만 발생되는 예외들에 타 로직 레이어 (컨트롤러, 서비스)가 의존관계를 지게 된다는 것이다. (무조건 예외를 던지거나, 잡아서 처리를 해야하기 때문에)
         * - SQLException은 JDBC에서 발생될 수 있는 예외인데, 향후 JPA로 기술을 바꾸는 경우 타 로직 레이어까지 수정을 해야하는 번거로 움이 생긴다.
         * - 이렇게 발생되는 예외는 대부분 복구가 불가능한 시스템 예외들이다. (DB장애, 네트워크 장비 장애 등) 즉, 불피요한 의존관계가 문제이다.
         * - 그렇다고 최상위 예외인 Exception을 던저버리면, 체크예외를 체크할 수 있는 기능이 무효화 되어버리고, 로직 상 중요한 예외가 무시 될수 있어서 위험히다.
         *
         * @throws SQLException
         * @throws ConnectException
         */

        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {

        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("예외 발생");
        }
    }

    static class Repository {

        public void call() throws SQLException {
            throw new SQLException("예외 발생");
        }
    }
}
