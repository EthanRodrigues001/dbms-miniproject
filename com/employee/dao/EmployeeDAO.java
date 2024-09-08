package com.employee.dao;

import com.employee.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    
    public void addEmployee(Employee employee) throws SQLException {
        String query = "INSERT INTO employees (name, email, department, salary, join_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getDepartment());
            stmt.setDouble(4, employee.getSalary());
            stmt.setDate(5, Date.valueOf(employee.getJoinDate()));
            stmt.executeUpdate();
        }
    }

   
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setDepartment(rs.getString("department"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setJoinDate(rs.getString("join_date"));
                employees.add(employee);
            }
        }
        return employees;
    }

    
    public void updateEmployee(Employee employee) throws SQLException {
        String query = "UPDATE employees SET name = ?, email = ?, department = ?, salary = ?, join_date = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getDepartment());
            stmt.setDouble(4, employee.getSalary());
            stmt.setDate(5, Date.valueOf(employee.getJoinDate()));
            stmt.setInt(6, employee.getId());
            stmt.executeUpdate();
        }
    }

    
    public void deleteEmployee(int id) throws SQLException {
        String query = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    
    public Employee getEmployeeById(int id) throws SQLException {
        String query = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setName(rs.getString("name"));
                    employee.setEmail(rs.getString("email"));
                    employee.setDepartment(rs.getString("department"));
                    employee.setSalary(rs.getDouble("salary"));
                    employee.setJoinDate(rs.getString("join_date"));
                    return employee;
                }
            }
        }
        return null;
    }
}
