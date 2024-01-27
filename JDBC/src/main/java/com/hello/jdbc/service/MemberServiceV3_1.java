package com.hello.jdbc.service;


import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저를 사용하여 트랜잭션를 관리함
 * 개선 포인트 - V2에선 비즈니스 로직과 트랜잭션을 관리하는 로직이 섞여있는데, PlatformTransactionManager를 사용하여
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

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

        // 트랜잭션이 시작됨
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {

            // 비즈니스 로직 시작
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); // 성공시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status);// 실패시 롤백
            throw new IllegalStateException(e);
        }
        // transactionManager에서 리소스 정리를 알아서 해준다.
        //        finally {
        //            release(con);
        //        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }
}
