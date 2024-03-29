package com.hello.jdbc.exception.translator;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.Exception.MyDbException;
import com.hello.jdbc.repository.Exception.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExceptionTranslatorV1Test {

    Repository repository;
    Serivce service;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Serivce(repository);
    }

    @Test
    void duplicatedKeySave() {
        service.createMember("memberId");
        service.createMember("memberId"); // 중복 키 발생
    }

    @RequiredArgsConstructor
    static class Serivce {
        private final Repository repository;

        public void createMember(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("saveId={}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복 발생, 복구 시도");
                String retryId = generateNewMemberId(memberId);
                log.info("retyrId = {}", retryId);
                repository.save(new Member(retryId, 0));
            }

        }

        private String generateNewMemberId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {

            String sql = "insert into member(member_id, money) values(?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {

                //h2 db인 경우
                if (e.getErrorCode() == 23505) {
                    throw new MyDuplicateKeyException(e);
                } else throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }
}
