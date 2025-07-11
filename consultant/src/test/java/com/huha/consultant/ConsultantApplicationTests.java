package com.huha.consultant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ConsultantApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("连接成功！Database: " + conn.getMetaData().getDatabaseProductName());
        }
    }

    @Test
    void contextLoads() {
    }

}
