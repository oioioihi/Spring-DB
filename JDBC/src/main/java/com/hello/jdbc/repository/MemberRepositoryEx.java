package com.hello.jdbc.repository;

import com.hello.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryEx {
    // 체크예외를 사용하면 인터페이스에서도 해당 예외가 선언되어 있어야하는 단점이 있다.
    // 특정 기술에 종속적이지 않게 하기위해서 인터페이스를 선언한건데, 예외선언 때문에 JDBC 기술에 종속적인 인터페이스가 되어버렸다.
    Member save(Member member) throws SQLException;

    Member findById(String memberId) throws SQLException;

    void update(String memberId, int money) throws SQLException;

    void delete(String memberId) throws SQLException;
}
