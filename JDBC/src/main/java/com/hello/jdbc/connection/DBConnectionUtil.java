package com.hello.jdbc.connection;

import static com.hello.jdbc.connection.ConnectionConst.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConnectionUtil {
	public static Connection getConnection(){
		try {
			Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			log.info("get connection = {}, class = {}",connection,connection.getClass());
			// connection.getClass() => Returns the runtime class of this Object. ex) mysql, oracle
			return connection;
		}catch (SQLException e){
			throw new IllegalStateException(e);
		}
	}
}
