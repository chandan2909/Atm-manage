package Atm.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connn {
    private static final String URL = "jdbc:mysql://localhost:3306/atm?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "root";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        conn.setAutoCommit(true); // Ensure auto-commit is enabled
        return conn;
    }
}
