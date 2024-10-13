package src.employee.management.system;

import java.sql.*;

public class Conn {
    private Connection con;
    private Statement s;

    public Conn() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish a connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/employee_management", "root", "e@098");
            s = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get the connection
    public Connection getConnection() {
        return con;
    }

    public Statement getStatement() {
        return s;
    }
}