package com.hello.jdbc.service;


import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // auto commit이 false인 상태로 커넥션 풀에 반납되면, 다음 사용시에도 동일하게 유지가 되어서 의도치 않는 장애가 날수 있다.
                con.close(); // 커넥션 풀에 반납
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    // 트랜잭션 보장은 되지만 비즈니스로직과 트랜잭션 내용이 함께 섞여있다는 단점이 있다.
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); // 트랜잭션 시작

            // 비즈니스 로직 시작
            Member fromMember = memberRepository.findById(con, fromId);
            Member toMember = memberRepository.findById(con, toId);
            memberRepository.update(con, fromId, fromMember.getMoney() - money);
            validation(toMember);
            memberRepository.update(con, toId, toMember.getMoney() + money);

            con.commit(); // 성공시 커밋
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }
}
