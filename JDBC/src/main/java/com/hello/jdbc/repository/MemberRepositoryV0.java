package com.hello.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import com.hello.jdbc.connection.DBConnectionUtil;
import com.hello.jdbc.domain.Member;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC - DriveManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

	 public Member save(Member member) throws SQLException {
		 String sql = "insert into member(member_id,money) values (?,?)";

		 Connection con = null;
		 PreparedStatement  preparedStatement = null;

		 try {
			 con = getConnection();
			 preparedStatement = con.prepareStatement(sql);
			 preparedStatement.setString(1, member.getMemberId());
			 preparedStatement.setInt(2, member.getMoney());
			 int effectedRowCount = preparedStatement.executeUpdate();
			 return member;
		 }catch (SQLException e){
			 log.error("db error",e);
			 throw  e;
		 }finally {
			 // 항상 호출하는 것이 보장 되도록 finally에서 자원 반납 실행, 또는 try with resource
			 // 자원 반납
			 close(con,preparedStatement,null);
		 }

	 }

	 public Member findById(String memberId)throws SQLException{

		 String sql = "select * from member where member_id = ?";
		 Connection con = null;
		 PreparedStatement preparedStatement = null;
		 ResultSet rs = null;

		 try {
			 con = getConnection();
			 preparedStatement = con.prepareStatement(sql);
			 preparedStatement.setString(1,memberId);
			 rs = preparedStatement.executeQuery();

			 if(rs.next()){ // 최초의 커서는 데이터를 가리키고 있지 않기 때문에, 실제 데이터가 있는 로우로 커서를 이동

				 Member member = new Member();
				 member.setMemberId(rs.getString("member_id"));
				 member.setMoney(rs.getInt("money"));
				 return member;
			 }else{
				 throw new NoSuchElementException("member not found memberId=" + memberId);
			 }
		 }
		 catch (SQLException e) {
			log.error("db error",e);
			throw e;
		 }
		 finally {
			 close(con,preparedStatement,rs);
		 }
	 }

	 public void update(String memberId, int money) throws SQLException {
		 String sql = "update member set money=? where member_id=?";
		 Connection con = null;
		 PreparedStatement preparedStatement = null;

		 try {
			 con = getConnection();
			 preparedStatement = con.prepareStatement(sql);
			 preparedStatement.setInt(1, money);
			 preparedStatement.setString(2, memberId);
			 int resultSize = preparedStatement.executeUpdate();
			 log.info("resultSize = {}",resultSize);
		 }catch (SQLException e){
			 log.error("db error",e);
			 throw  e;
		 }finally {
			 close(con,preparedStatement,null);
		 }

	 }

	public void delete(String memberId) throws SQLException {
		String sql = "delete from member where member_id=?";
		Connection con = null;
		PreparedStatement preparedStatement = null;

		try {
			con = getConnection();
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, memberId);
			int resultSize = preparedStatement.executeUpdate();
		}catch (SQLException e){
			log.error("db error",e);
			throw  e;
		}finally {
			close(con,preparedStatement,null);
		}

	}

	private static Connection getConnection() {
		return DBConnectionUtil.getConnection();
	}

	private void close (Connection con, Statement statement, ResultSet rs){
		/**
		 *  자원 반납시 Exception이 발생하면 다음 자원 반납이 진행 되지 않으므로,
		 *  각각 자원 반납 로직을 격리 시켜주어야 한다.
		 */
		 if( statement != null){
			 try {
				 statement.close();
			 } catch (SQLException e) {
				 log.info("statement error",e);
			 }
		 }
		 if( con != null){
			 try {
				 con.close();
			 } catch (SQLException e) {
				 log.info("error",e);
			 }
		 }

		 if(rs != null){
			 try {
				 rs.close();
			 } catch (SQLException e) {
				 log.info("error",e);
			 }
		 }
	}
}
