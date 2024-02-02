package com.hello.jdbc.repository;

import com.hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLException Translator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {
    private final DataSource dataSource;
    private final SQLExceptionTranslator exceptionTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id,money) values (?,?)";

        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            int effectedRowCount = preparedStatement.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw exceptionTranslator.translate("save 예외", sql, e);
        } finally {
            // 항상 호출하는 것이 보장 되도록 finally에서 자원 반납 실행, 또는 try with resource
            // 자원 반납
            close(con, preparedStatement, null);
        }

    }

    @Override
    public Member findById(String memberId) {

        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            rs = preparedStatement.executeQuery();

            if (rs.next()) { // 최초의 커서는 데이터를 가리키고 있지 않기 때문에, 실제 데이터가 있는 로우로 커서를 이동

                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw exceptionTranslator.translate("find 예외", sql, e);
        } finally {
            close(con, preparedStatement, rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultSize = preparedStatement.executeUpdate();
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw exceptionTranslator.translate("update 예외", sql, e);

        } finally {
            close(con, preparedStatement, null);
        }
    }


    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = getConnection();
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            int resultSize = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw exceptionTranslator.translate("delete 예외", sql, e);

        } finally {
            close(con, preparedStatement, null);
        }

    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        // TransactionSynchronizationManager가 관리하는 커넥션이 있는 경우 반환하고, 없으면 새로운 커넥션을 생성해서 반환한다.
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}", connection, connection.getClass());
        return connection;
    }

    private void close(Connection con, Statement statement, ResultSet rs) {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(statement);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다.
        // 트랜잭션을 사용하기 위해 동기화된 커넥션은 커넥션을 닫지 않고 그대로 유지하고, 아닌 경우에는 해당 커넥션을 닫아버린다.
        DataSourceUtils.releaseConnection(con, dataSource);

    }
}
