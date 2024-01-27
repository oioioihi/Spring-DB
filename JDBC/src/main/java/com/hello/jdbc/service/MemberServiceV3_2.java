package com.hello.jdbc.service;


import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 * 개선 포인트 - V3_1의 'accountTransfer'함수를 살펴보면 비즈니스로직을 트랜잭션 관련 로직이 둘러싸고있다.
 * 만약 다른 메서드들이 추가 된다면 동일한 로직(트랜잭션 처리로직)이 계속 들어가게 될것인데, 이 부분은 트랜잭션 탬플릿(템플릿 콜백 패턴)를 사용하여 개선 가능하다.
 */
@Slf4j
public class MemberServiceV3_2 {


    private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

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


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        transactionTemplate.executeWithoutResult((transactionStatus -> {
            // 비즈니스 로직 시작
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }));
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }
}
